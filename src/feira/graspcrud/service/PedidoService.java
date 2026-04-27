package feira.graspcrud.service;

import feira.graspcrud.domain.Pedido;
import feira.graspcrud.domain.StatusPedido;
import feira.graspcrud.dto.PedidoRequest;
import feira.graspcrud.exception.RegraNegocioException;
import feira.graspcrud.repository.PedidoRepository;
import feira.graspcrud.repository.StatusPedidoRepository;

import java.util.List;

/**
 * Serviço de aplicação responsável pelos casos de uso de Pedido.
 *
 * <p>Padrão GRASP: Low Coupling — depende de {@link PedidoRepository} e
 * {@link StatusPedidoRepository} por suas interfaces, sem acoplar-se
 * à implementação concreta de persistência.
 *
 * <p>Padrão GRASP: High Cohesion — cada método corresponde a exatamente
 * um caso de uso da aplicação.
 *
 * <p>Padrão GRASP: Creator — instancia objetos {@link Pedido} a partir
 * do DTO de request, pois possui todas as informações necessárias para isso.
 */
public class PedidoService {

    private final PedidoRepository pedidoRepo;
    private final StatusPedidoRepository statusRepo;

    /**
     * Constrói o serviço com as dependências necessárias.
     *
     * @param pedidoRepo repositório de Pedido
     * @param statusRepo repositório de StatusPedido
     */
    public PedidoService(PedidoRepository pedidoRepo, StatusPedidoRepository statusRepo) {
        this.pedidoRepo = pedidoRepo;
        this.statusRepo = statusRepo;
    }

    /**
     * Cadastra um novo Pedido associado a um StatusPedido existente.
     *
     * <p>Regras aplicadas:
     * <ul>
     *   <li>Nome deve ter ao menos 3 caracteres.</li>
     *   <li>StatusPedido é obrigatório e deve existir no cadastro.</li>
     * </ul>
     *
     * @param request dados de entrada para criação
     * @return o Pedido criado com id e data de criação preenchidos
     * @throws RegraNegocioException se alguma regra for violada
     */
    public Pedido cadastrar(PedidoRequest request) {
        StatusPedido status = resolverStatus(request.getStatusPedidoId());
        Pedido pedido = new Pedido(null, request.getNome(), request.getDescricao(), request.getAtivo(), status);
        pedido.validar();
        return pedidoRepo.salvar(pedido);
    }

    /**
     * Lista todos os pedidos cadastrados.
     *
     * @return lista de pedidos
     */
    public List<Pedido> listarTodos() {
        return pedidoRepo.listarTodos();
    }

    /**
     * Busca um Pedido pelo seu id.
     *
     * @param id o identificador do pedido
     * @return o Pedido encontrado
     * @throws RegraNegocioException se não encontrado
     */
    public Pedido buscarPorId(Long id) {
        return pedidoRepo.buscarPorId(id)
                .orElseThrow(() -> new RegraNegocioException("Pedido não encontrado com id: " + id));
    }

    /**
     * Atualiza os campos básicos de um Pedido (nome, descrição, ativo).
     *
     * <p>Nota: a atualização de status segue regras específicas de transição
     * e é feita pelo método {@link #avancarStatus(Long, Long)}.
     *
     * <p>Regras aplicadas:
     * <ul>
     *   <li>Pedido deve existir.</li>
     *   <li>Nome deve ter ao menos 3 caracteres.</li>
     * </ul>
     *
     * @param id      id do pedido a atualizar
     * @param request novos dados
     * @return o Pedido atualizado
     * @throws RegraNegocioException se alguma regra for violada
     */
    public Pedido atualizar(Long id, PedidoRequest request) {
        Pedido pedido = buscarPorId(id);

        pedido.setNome(request.getNome());
        pedido.setDescricao(request.getDescricao());
        if (request.getAtivo() != null) {
            pedido.setAtivo(request.getAtivo());
        }
        pedido.validar();
        return pedidoRepo.salvar(pedido);
    }

    /**
     * Avança o status de um Pedido para um novo StatusPedido,
     * respeitando a regra de transição progressiva.
     *
     * <p>A transição só é permitida se o novo status tiver ordem de transição
     * maior que o status atual. Retroceder não é permitido.
     *
     * @param pedidoId    id do pedido
     * @param novoStatusId id do novo StatusPedido
     * @return o Pedido com status atualizado
     * @throws RegraNegocioException se a transição for inválida
     */
    public Pedido avancarStatus(Long pedidoId, Long novoStatusId) {
        Pedido pedido = buscarPorId(pedidoId);
        StatusPedido novoStatus = resolverStatus(novoStatusId);
        pedido.avancarStatus(novoStatus);
        return pedidoRepo.salvar(pedido);
    }

    /**
     * Remove um Pedido pelo seu id.
     *
     * @param id o identificador do pedido a remover
     * @throws RegraNegocioException se o pedido não existir
     */
    public void remover(Long id) {
        buscarPorId(id); // valida existência
        pedidoRepo.remover(id);
    }

    /**
     * Resolve um StatusPedido a partir do seu id, lançando exceção se não encontrado.
     *
     * @param statusId o id do status
     * @return o StatusPedido encontrado
     * @throws RegraNegocioException se o id for nulo ou o status não existir
     */
    private StatusPedido resolverStatus(Long statusId) {
        if (statusId == null) {
            throw new RegraNegocioException("StatusPedido é obrigatório.");
        }
        return statusRepo.buscarPorId(statusId)
                .orElseThrow(() -> new RegraNegocioException("StatusPedido não encontrado com id: " + statusId));
    }
}
