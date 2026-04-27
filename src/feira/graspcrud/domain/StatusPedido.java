package feira.graspcrud.domain;

import feira.graspcrud.exception.RegraNegocioException;

/**
 * Entidade de domínio que representa um status de pedido na feira livre.
 *
 * <p>Padrão GRASP: Information Expert — esta classe conhece e valida
 * suas próprias regras de consistência, como nome obrigatório e ordem de transição.
 *
 * <p>A ordem ({@code ordemTransicao}) define a sequência obrigatória de progressão
 * dos status. Um pedido só pode avançar para um status com ordem maior,
 * nunca retroceder.
 */
public class StatusPedido {

    private Long id;
    private String nome;
    private String descricao;
    private int ordemTransicao;

    /** Construtor padrão necessário para desserialização JSON. */
    public StatusPedido() {}

    /**
     * Cria um StatusPedido com todos os campos obrigatórios.
     *
     * @param id              identificador único
     * @param nome            nome do status (obrigatório, único)
     * @param descricao       descrição opcional
     * @param ordemTransicao  posição na sequência de transição (ex.: 1, 2, 3)
     */
    public StatusPedido(Long id, String nome, String descricao, int ordemTransicao) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.ordemTransicao = ordemTransicao;
    }

    /**
     * Valida as regras de consistência internas deste StatusPedido.
     *
     * <p>Regras verificadas:
     * <ul>
     *   <li>Nome é obrigatório e deve ter no mínimo 3 caracteres.</li>
     *   <li>Ordem de transição deve ser maior que zero.</li>
     * </ul>
     *
     * @throws RegraNegocioException se alguma regra for violada
     */
    public void validar() {
        if (nome == null || nome.trim().length() < 3) {
            throw new RegraNegocioException("Nome do StatusPedido é obrigatório e deve ter ao menos 3 caracteres.");
        }
        if (ordemTransicao <= 0) {
            throw new RegraNegocioException("Ordem de transição deve ser maior que zero.");
        }
    }

    /**
     * Verifica se este status pode suceder o status informado,
     * respeitando a regra de transição progressiva (nunca retroceder).
     *
     * @param statusAtual o status atual do pedido
     * @return {@code true} se a transição for permitida
     */
    public boolean podeSucceder(StatusPedido statusAtual) {
        if (statusAtual == null) return true;
        return this.ordemTransicao > statusAtual.getOrdemTransicao();
    }

    // --- Getters e Setters ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public int getOrdemTransicao() { return ordemTransicao; }
    public void setOrdemTransicao(int ordemTransicao) { this.ordemTransicao = ordemTransicao; }

    @Override
    public String toString() {
        return String.format("[%d] %s (ordem: %d)%s",
                id, nome, ordemTransicao,
                descricao != null && !descricao.isBlank() ? " — " + descricao : "");
    }
}
