package feira.graspcrud.service;

import feira.graspcrud.domain.StatusPedido;
import feira.graspcrud.dto.StatusPedidoRequest;
import feira.graspcrud.exception.RegraNegocioException;
import feira.graspcrud.repository.PedidoRepository;
import feira.graspcrud.repository.StatusPedidoRepository;

import java.util.List;

/**
 * Serviço de aplicação responsável pelos casos de uso de StatusPedido.
 *
 * <p>Padrão GRASP: Low Coupling — depende de {@link StatusPedidoRepository} e
 * {@link PedidoRepository} por suas interfaces, sem conhecer a implementação
 * concreta de persistência.
 *
 * <p>Padrão GRASP: High Cohesion — cada método realiza um único caso de uso,
 * sem misturar responsabilidades.
 *
 * <p>Padrão GRASP: Creator — instancia objetos de domínio {@link StatusPedido}
 * a partir dos dados recebidos via DTO, pois possui todas as informações necessárias.
 */
public class StatusPedidoService {

    private final StatusPedidoRepository statusRepo;
    private final PedidoRepository pedidoRepo;

    /**
     * Constrói o serviço com as dependências injetadas.
     *
     * @param statusRepo repositório de StatusPedido
     * @param pedidoRepo repositório de Pedido (para validar remoção)
     */
    public StatusPedidoService(StatusPedidoRepository statusRepo, PedidoRepository pedidoRepo) {
        this.statusRepo = statusRepo;
        this.pedidoRepo = pedidoRepo;
    }

    /**
     * Cadastra um novo StatusPedido.
     *
     * <p>Regras aplicadas:
     * <ul>
     *   <li>Nome deve ter ao menos 3 caracteres.</li>
     *   <li>Nome deve ser único no cadastro (case-insensitive).</li>
     *   <li>Ordem de transição deve ser maior que zero.</li>
     * </ul>
     *
     * @param request dados de entrada para criação
     * @return o StatusPedido criado com id preenchido
     * @throws RegraNegocioException se alguma regra for violada
     */
    public StatusPedido cadastrar(StatusPedidoRequest request) {
        verificarNomeUnico(request.getNome(), null);

        StatusPedido status = new StatusPedido(null, request.getNome(), request.getDescricao(), request.getOrdemTransicao());
        status.validar();
        return statusRepo.salvar(status);
    }

    /**
     * Lista todos os StatusPedido cadastrados, ordenados por ordem de transição.
     *
     * @return lista de status
     */
    public List<StatusPedido> listarTodos() {
        return statusRepo.listarTodos();
    }

    /**
     * Busca um StatusPedido por id.
     *
     * @param id o identificador do status
     * @return o StatusPedido encontrado
     * @throws RegraNegocioException se não encontrado
     */
    public StatusPedido buscarPorId(Long id) {
        return statusRepo.buscarPorId(id)
                .orElseThrow(() -> new RegraNegocioException("StatusPedido não encontrado com id: " + id));
    }

    /**
     * Atualiza os dados de um StatusPedido existente.
     *
     * <p>Regras aplicadas:
     * <ul>
     *   <li>StatusPedido deve existir.</li>
     *   <li>Nome deve ter ao menos 3 caracteres.</li>
     *   <li>Novo nome não pode conflitar com outro status (exceto o próprio).</li>
     * </ul>
     *
     * @param id      id do status a atualizar
     * @param request novos dados
     * @return o StatusPedido atualizado
     * @throws RegraNegocioException se alguma regra for violada
     */
    public StatusPedido atualizar(Long id, StatusPedidoRequest request) {
        StatusPedido status = buscarPorId(id);
        verificarNomeUnico(request.getNome(), id);

        status.setNome(request.getNome());
        status.setDescricao(request.getDescricao());
        status.setOrdemTransicao(request.getOrdemTransicao());
        status.validar();
        return statusRepo.salvar(status);
    }

    /**
     * Remove um StatusPedido pelo id.
     *
     * <p>Regra: não é permitido remover um StatusPedido que esteja
     * em uso por algum Pedido cadastrado.
     *
     * @param id o identificador do status a remover
     * @throws RegraNegocioException se o status estiver em uso ou não existir
     */
    public void remover(Long id) {
        buscarPorId(id); // valida existência
        if (pedidoRepo.existePorStatusPedidoId(id)) {
            throw new RegraNegocioException(
                    "Não é possível remover este StatusPedido pois ele está em uso por um ou mais pedidos.");
        }
        statusRepo.remover(id);
    }

    /**
     * Verifica se o nome já está em uso por outro StatusPedido.
     *
     * @param nome     nome a verificar
     * @param idAtual  id do status sendo atualizado (null para novos cadastros)
     * @throws RegraNegocioException se o nome já existir em outro registro
     */
    private void verificarNomeUnico(String nome, Long idAtual) {
        if (nome == null || nome.isBlank()) return; // validar() cuidará disso com mensagem amigável
        statusRepo.buscarPorNome(nome).ifPresent(existente -> {
            if (!existente.getId().equals(idAtual)) {
                throw new RegraNegocioException("Já existe um StatusPedido com o nome: " + nome);
            }
        });
    }
}
