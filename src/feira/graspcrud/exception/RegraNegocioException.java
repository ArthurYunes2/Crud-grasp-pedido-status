package feira.graspcrud.exception;

/**
 * Exceção de domínio lançada quando uma regra de negócio é violada.
 *
 * <p>Padrão GRASP: Information Expert / Low Coupling — esta exceção
 * não possui dependência de infraestrutura, pertence exclusivamente
 * ao domínio da aplicação e pode ser lançada por qualquer camada
 * sem criar acoplamento com frameworks ou bibliotecas externas.
 */
public class RegraNegocioException extends RuntimeException {

    /**
     * Cria uma exceção de regra de negócio com a mensagem informada.
     *
     * @param mensagem descrição clara da regra violada
     */
    public RegraNegocioException(String mensagem) {
        super(mensagem);
    }
}
