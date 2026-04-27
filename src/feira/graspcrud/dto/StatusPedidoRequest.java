package feira.graspcrud.dto;

/**
 * DTO de entrada para criação e atualização de StatusPedido.
 *
 * <p>Padrão GRASP: Low Coupling — isola os dados de entrada da camada
 * de domínio, evitando que o controller manipule diretamente as entidades.
 */
public class StatusPedidoRequest {

    private String nome;
    private String descricao;
    private int ordemTransicao;

    /** Construtor padrão. */
    public StatusPedidoRequest() {}

    /**
     * Cria um request com todos os campos.
     *
     * @param nome            nome do status
     * @param descricao       descrição opcional
     * @param ordemTransicao  posição na sequência de transição
     */
    public StatusPedidoRequest(String nome, String descricao, int ordemTransicao) {
        this.nome = nome;
        this.descricao = descricao;
        this.ordemTransicao = ordemTransicao;
    }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public int getOrdemTransicao() { return ordemTransicao; }
    public void setOrdemTransicao(int ordemTransicao) { this.ordemTransicao = ordemTransicao; }
}
