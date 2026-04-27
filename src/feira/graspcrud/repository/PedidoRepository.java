package feira.graspcrud.repository;

import feira.graspcrud.domain.Pedido;
import java.util.List;
import java.util.Optional;

/**
 * Interface de repositório para Pedido.
 *
 * <p>Padrão GRASP: Protected Variations — isola os serviços da implementação
 * concreta de persistência, permitindo substituição sem impacto no domínio.
 *
 * <p>Padrão GRASP: Indirection — age como intermediário entre a camada
 * de serviço e a infraestrutura de armazenamento.
 */
public interface PedidoRepository {

    /**
     * Salva um novo Pedido ou atualiza um existente.
     *
     * @param pedido o pedido a ser persistido
     * @return o pedido salvo com id preenchido
     */
    Pedido salvar(Pedido pedido);

    /**
     * Retorna todos os pedidos cadastrados.
     *
     * @return lista de pedidos
     */
    List<Pedido> listarTodos();

    /**
     * Busca um Pedido pelo seu identificador único.
     *
     * @param id o identificador
     * @return Optional contendo o pedido, ou vazio se não encontrado
     */
    Optional<Pedido> buscarPorId(Long id);

    /**
     * Verifica se existe algum Pedido associado ao StatusPedido informado.
     *
     * @param statusPedidoId o id do StatusPedido
     * @return {@code true} se houver ao menos um pedido com esse status
     */
    boolean existePorStatusPedidoId(Long statusPedidoId);

    /**
     * Remove um Pedido pelo seu identificador.
     *
     * @param id o identificador do pedido a remover
     */
    void remover(Long id);
}
