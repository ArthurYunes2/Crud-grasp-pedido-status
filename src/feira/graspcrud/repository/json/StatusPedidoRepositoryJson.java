package feira.graspcrud.repository.json;

import feira.graspcrud.domain.StatusPedido;
import feira.graspcrud.repository.StatusPedidoRepository;
import feira.graspcrud.util.JsonMini;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementação de {@link StatusPedidoRepository} com persistência em arquivo JSON.
 *
 * <p>Padrão GRASP: Pure Fabrication — esta classe não representa um conceito
 * do domínio da feira livre; foi fabricada para manter a infraestrutura de
 * persistência separada do domínio.
 *
 * <p>Padrão GRASP: Indirection — implementa a interface de repositório,
 * servindo de intermediário entre o serviço e o sistema de arquivos.
 *
 * <p>Os dados são carregados do arquivo JSON na construção do objeto
 * e persistidos imediatamente após cada operação de escrita.
 */
public class StatusPedidoRepositoryJson implements StatusPedidoRepository {

    private static final String ARQUIVO = "data/status-pedido.json";

    private final Map<Long, StatusPedido> cache = new LinkedHashMap<>();
    private long proximoId = 1L;

    /**
     * Constrói o repositório carregando os dados do arquivo JSON, se existir.
     */
    public StatusPedidoRepositoryJson() {
        carregarDoArquivo();
    }

    /**
     * {@inheritDoc}
     * Atribui id automaticamente se o status for novo (id nulo).
     */
    @Override
    public StatusPedido salvar(StatusPedido status) {
        if (status.getId() == null) {
            status.setId(proximoId++);
        } else if (status.getId() >= proximoId) {
            proximoId = status.getId() + 1;
        }
        cache.put(status.getId(), status);
        salvarNoArquivo();
        return status;
    }

    /**
     * {@inheritDoc}
     * Retorna a lista ordenada por ordem de transição crescente.
     */
    @Override
    public List<StatusPedido> listarTodos() {
        return cache.values().stream()
                .sorted(Comparator.comparingInt(StatusPedido::getOrdemTransicao))
                .collect(Collectors.toList());
    }

    /** {@inheritDoc} */
    @Override
    public Optional<StatusPedido> buscarPorId(Long id) {
        return Optional.ofNullable(cache.get(id));
    }

    /** {@inheritDoc} */
    @Override
    public Optional<StatusPedido> buscarPorNome(String nome) {
        if (nome == null) return Optional.empty();
        return cache.values().stream()
                .filter(s -> s.getNome() != null && s.getNome().equalsIgnoreCase(nome))
                .findFirst();
    }

    /** {@inheritDoc} */
    @Override
    public void remover(Long id) {
        cache.remove(id);
        salvarNoArquivo();
    }

    /**
     * Carrega os dados do arquivo JSON para o cache em memória.
     * Chamado apenas na construção do repositório.
     */
    private void carregarDoArquivo() {
        String json = JsonMini.lerArquivo(ARQUIVO);
        if (json.isBlank()) return;

        List<Map<String, Object>> lista = JsonMini.desserializarLista(json);
        for (Map<String, Object> mapa : lista) {
            StatusPedido s = mapParaStatus(mapa);
            cache.put(s.getId(), s);
            if (s.getId() >= proximoId) proximoId = s.getId() + 1;
        }
    }

    /**
     * Persiste o conteúdo atual do cache no arquivo JSON.
     * Chamado após cada operação de escrita.
     */
    private void salvarNoArquivo() {
        List<Map<String, Object>> lista = cache.values().stream()
                .map(this::statusParaMap)
                .collect(Collectors.toList());
        JsonMini.escreverArquivo(ARQUIVO, JsonMini.serializarLista(lista));
    }

    /**
     * Converte um StatusPedido em mapa para serialização JSON.
     *
     * @param s o status a converter
     * @return mapa com os campos do status
     */
    private Map<String, Object> statusParaMap(StatusPedido s) {
        Map<String, Object> mapa = new LinkedHashMap<>();
        mapa.put("id", s.getId());
        mapa.put("nome", s.getNome());
        mapa.put("descricao", s.getDescricao());
        mapa.put("ordemTransicao", s.getOrdemTransicao());
        return mapa;
    }

    /**
     * Converte um mapa lido do JSON em um objeto StatusPedido.
     *
     * @param mapa mapa com os campos lidos do JSON
     * @return StatusPedido reconstruído
     */
    private StatusPedido mapParaStatus(Map<String, Object> mapa) {
        return new StatusPedido(
                JsonMini.toLong(mapa.get("id")),
                JsonMini.toStr(mapa.get("nome")),
                JsonMini.toStr(mapa.get("descricao")),
                JsonMini.toInt(mapa.get("ordemTransicao"))
        );
    }
}
