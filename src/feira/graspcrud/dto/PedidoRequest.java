package feira.graspcrud.dto;

/**
 * DTO de entrada para criação e atualização de Pedido.
 *
 * <p>Padrão GRASP: Low Coupling — transporta dados do controller para o serviço
 * sem expor a entidade de domínio diretamente à camada de entrada.
 */
public class PedidoRequest {

    private String nome;
    private String descricao;
    private Boolean ativo;
    private Long statusPedidoId;

    /** Construtor padrão. */
    public PedidoRequest() {}

    /**
     * Cria um request com todos os campos.
     *
     * @param nome          nome do pedido
     * @param descricao     descrição opcional
     * @param ativo         se o pedido está ativo
     * @param statusPedidoId id do StatusPedido associado
     */
    public PedidoRequest(String nome, String descricao, Boolean ativo, Long statusPedidoId) {
        this.nome = nome;
        this.descricao = descricao;
        this.ativo = ativo;
        this.statusPedidoId = statusPedidoId;
    }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public Boolean getAtivo() { return ativo; }
    public void setAtivo(Boolean ativo) { this.ativo = ativo; }

    public Long getStatusPedidoId() { return statusPedidoId; }
    public void setStatusPedidoId(Long statusPedidoId) { this.statusPedidoId = statusPedidoId; }
}
