package feira.graspcrud.domain;

import feira.graspcrud.exception.RegraNegocioException;
import java.time.LocalDate;

/**
 * Entidade de domínio que representa um pedido da feira livre.
 *
 * <p>Padrão GRASP: Information Expert — esta classe é responsável por
 * validar suas próprias regras de negócio, como nome mínimo, status obrigatório
 * e a regra de transição progressiva de status (nunca retroceder).
 *
 * <p>A data de criação é preenchida automaticamente no momento da instanciação
 * e não pode ser alterada externamente.
 */
public class Pedido {

    private Long id;
    private String nome;
    private String descricao;
    private Boolean ativo;
    private LocalDate dataCriacao;
    private StatusPedido statusPedido;

    /** Construtor padrão necessário para desserialização JSON. */
    public Pedido() {}

    /**
     * Cria um Pedido com data de criação definida automaticamente como hoje.
     *
     * @param id          identificador único
     * @param nome        nome do pedido
     * @param descricao   descrição opcional
     * @param ativo       indica se o pedido está ativo
     * @param statusPedido status inicial obrigatório
     */
    public Pedido(Long id, String nome, String descricao, Boolean ativo, StatusPedido statusPedido) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.ativo = ativo != null ? ativo : true;
        this.dataCriacao = LocalDate.now();
        this.statusPedido = statusPedido;
    }

    /**
     * Valida as regras de consistência do pedido.
     *
     * <p>Regras verificadas:
     * <ul>
     *   <li>Nome é obrigatório e deve ter no mínimo 3 caracteres.</li>
     *   <li>StatusPedido é obrigatório.</li>
     * </ul>
     *
     * @throws RegraNegocioException se alguma regra for violada
     */
    public void validar() {
        if (nome == null || nome.trim().length() < 3) {
            throw new RegraNegocioException("Nome do Pedido é obrigatório e deve ter ao menos 3 caracteres.");
        }
        if (statusPedido == null) {
            throw new RegraNegocioException("StatusPedido é obrigatório.");
        }
    }

    /**
     * Avança o status do pedido para o novo status informado,
     * respeitando a regra de transição progressiva.
     *
     * <p>A transição só é permitida se o novo status tiver ordem de transição
     * maior que o status atual. Retroceder de status não é permitido.
     *
     * @param novoStatus o status para o qual se deseja avançar
     * @throws RegraNegocioException se a transição for inválida (retrocesso ou mesmo status)
     */
    public void avancarStatus(StatusPedido novoStatus) {
        if (novoStatus == null) {
            throw new RegraNegocioException("Novo status não pode ser nulo.");
        }
        if (!novoStatus.podeSucceder(this.statusPedido)) {
            String msgAtual = this.statusPedido != null ? this.statusPedido.getNome() : "nenhum";
            throw new RegraNegocioException(
                    String.format("Transição inválida: não é permitido ir de '%s' para '%s'. O status só pode avançar.",
                            msgAtual, novoStatus.getNome()));
        }
        this.statusPedido = novoStatus;
    }

    // --- Getters e Setters ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public Boolean getAtivo() { return ativo; }
    public void setAtivo(Boolean ativo) { this.ativo = ativo; }

    public LocalDate getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDate dataCriacao) { this.dataCriacao = dataCriacao; }

    public StatusPedido getStatusPedido() { return statusPedido; }
    public void setStatusPedido(StatusPedido statusPedido) { this.statusPedido = statusPedido; }

    @Override
    public String toString() {
        return String.format("[%d] %s | Status: %s | Criado em: %s | Ativo: %s%s",
                id, nome,
                statusPedido != null ? statusPedido.getNome() : "N/A",
                dataCriacao,
                Boolean.TRUE.equals(ativo) ? "Sim" : "Não",
                descricao != null && !descricao.isBlank() ? " | " + descricao : "");
    }
}
