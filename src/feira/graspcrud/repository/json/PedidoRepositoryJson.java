package feira.graspcrud.repository.json;

import feira.graspcrud.domain.Pedido;
import feira.graspcrud.domain.StatusPedido;
import feira.graspcrud.repository.PedidoRepository;
import feira.graspcrud.repository.StatusPedidoRepository;
import feira.graspcrud.util.JsonMini;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementação de {@link PedidoRepository} com persistência em arquivo JSON.
 *
 * <p>Padrão GRASP: Pure Fabrication — classe fabricada para isolar a lógica
 * de persistência do domínio da aplicação.
 *
 * <p>Padrão GRASP: Indirection — implementa a interface de repositório,
 * desacoplando o serviço da infraestrutura de armazenamento.
 *
 * <p>Recebe {@link StatusPedidoRepository} por construtor para reconstruir
 * os objetos de domínio completos ao carregar os pedidos do JSON,
 * evitando duplicidade de dados no arquivo.
 */
public class PedidoRepositoryJson implements PedidoRepository {

    private static final String ARQUIVO = "data/pedidos.json";

    private final StatusPedidoRepository statusRepo;
    private final Map<Long, Pedido> cache = new LinkedHashMap<>();
    private long proximoId = 1L;

    /**
     * Constrói o repositório carregando os dados do arquivo JSON.
     *
     * @param statusRepo repositório de status, usado para recompor os relacionamentos
     */
    public PedidoRepositoryJson(StatusPedidoRepository statusRepo) {
        this.statusRepo = statusRepo;
        carregarDoArquivo();
    }

    /**
     * {@inheritDoc}
     * Atribui id automaticamente se o pedido for novo (id nulo).
     */
    @Override
    public Pedido salvar(Pedido pedido) {
        if (pedido.getId() == null) {
            pedido.setId(proximoId++);
        } else if (pedido.getId() >= proximoId) {
            proximoId = pedido.getId() + 1;
        }
        cache.put(pedido.getId(), pedido);
        salvarNoArquivo();
        return pedido;
    }

    /** {@inheritDoc} */
    @Override
    public List<Pedido> listarTodos() {
        return new ArrayList<>(cache.values());
    }

    /** {@inheritDoc} */
    @Override
    public Optional<Pedido> buscarPorId(Long id) {
        return Optional.ofNullable(cache.get(id));
    }

    /** {@inheritDoc} */
    @Override
    public boolean existePorStatusPedidoId(Long statusPedidoId) {
        return cache.values().stream()
                .anyMatch(p -> p.getStatusPedido() != null
                        && statusPedidoId.equals(p.getStatusPedido().getId()));
    }

    /** {@inheritDoc} */
    @Override
    public void remover(Long id) {
        cache.remove(id);
        salvarNoArquivo();
    }

    /**
     * Carrega os dados do arquivo JSON para o cache em memória.
     * O StatusPedido é resolvido via repositório para garantir consistência.
     */
    private void carregarDoArquivo() {
        String json = JsonMini.lerArquivo(ARQUIVO);
        if (json.isBlank()) return;

        List<Map<String, Object>> lista = JsonMini.desserializarLista(json);
        for (Map<String, Object> mapa : lista) {
            Pedido p = mapParaPedido(mapa);
            if (p != null) {
                cache.put(p.getId(), p);
                if (p.getId() >= proximoId) proximoId = p.getId() + 1;
            }
        }
    }

    /**
     * Persiste o conteúdo atual do cache no arquivo JSON.
     * Apenas o id do StatusPedido é salvo para evitar duplicidade.
     */
    private void salvarNoArquivo() {
        List<Map<String, Object>> lista = cache.values().stream()
                .map(this::pedidoParaMap)
                .collect(Collectors.toList());
        JsonMini.escreverArquivo(ARQUIVO, JsonMini.serializarLista(lista));
    }

    /**
     * Converte um Pedido em mapa para serialização JSON.
     * Persiste apenas o id do StatusPedido (referência, não duplicação).
     *
     * @param p o pedido a converter
     * @return mapa com os campos do pedido
     */
    private Map<String, Object> pedidoParaMap(Pedido p) {
        Map<String, Object> mapa = new LinkedHashMap<>();
        mapa.put("id", p.getId());
        mapa.put("nome", p.getNome());
        mapa.put("descricao", p.getDescricao());
        mapa.put("ativo", p.getAtivo());
        mapa.put("dataCriacao", p.getDataCriacao() != null ? p.getDataCriacao().toString() : null);
        mapa.put("statusPedidoId", p.getStatusPedido() != null ? p.getStatusPedido().getId() : null);
        return mapa;
    }

    /**
     * Converte um mapa lido do JSON em um objeto Pedido com StatusPedido resolvido.
     *
     * @param mapa mapa com os campos lidos do JSON
     * @return Pedido reconstruído, ou null se StatusPedido não for encontrado
     */
    private Pedido mapParaPedido(Map<String, Object> mapa) {
        Long statusId = JsonMini.toLong(mapa.get("statusPedidoId"));
        StatusPedido status = null;
        if (statusId != null) {
            status = statusRepo.buscarPorId(statusId).orElse(null);
            if (status == null) {
                System.err.println("[AVISO] StatusPedido id=" + statusId
                        + " referenciado no pedido id=" + mapa.get("id") + " não foi encontrado.");
            }
        }

        Pedido p = new Pedido();
        p.setId(JsonMini.toLong(mapa.get("id")));
        p.setNome(JsonMini.toStr(mapa.get("nome")));
        p.setDescricao(JsonMini.toStr(mapa.get("descricao")));
        p.setAtivo(JsonMini.toBoolean(mapa.get("ativo")));
        String dataStr = JsonMini.toStr(mapa.get("dataCriacao"));
        p.setDataCriacao(dataStr != null ? LocalDate.parse(dataStr) : LocalDate.now());
        p.setStatusPedido(status);
        return p;
    }
}
