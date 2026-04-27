package feira.graspcrud.repository;

import feira.graspcrud.domain.StatusPedido;
import java.util.List;
import java.util.Optional;

/**
 * Interface de repositório para StatusPedido.
 *
 * <p>Padrão GRASP: Protected Variations — ao depender desta abstração,
 * os serviços ficam isolados da implementação concreta de persistência.
 * Trocar JSON por outro mecanismo exige apenas criar uma nova implementação,
 * sem alterar domínio ou serviços.
 *
 * <p>Padrão GRASP: Indirection — serve de intermediário entre o serviço
 * e a camada de infraestrutura de persistência.
 */
public interface StatusPedidoRepository {

    /**
     * Salva um novo StatusPedido ou atualiza um existente.
     *
     * @param status o status a ser persistido
     * @return o status salvo com id preenchido
     */
    StatusPedido salvar(StatusPedido status);

    /**
     * Retorna todos os status cadastrados, ordenados por ordem de transição.
     *
     * @return lista de status
     */
    List<StatusPedido> listarTodos();

    /**
     * Busca um StatusPedido pelo seu identificador único.
     *
     * @param id o identificador
     * @return Optional contendo o status, ou vazio se não encontrado
     */
    Optional<StatusPedido> buscarPorId(Long id);

    /**
     * Busca um StatusPedido pelo nome (busca exata, case-insensitive).
     *
     * @param nome o nome a buscar
     * @return Optional contendo o status, ou vazio se não encontrado
     */
    Optional<StatusPedido> buscarPorNome(String nome);

    /**
     * Remove um StatusPedido pelo seu identificador.
     *
     * @param id o identificador do status a remover
     */
    void remover(Long id);
}
