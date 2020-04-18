/**
 * generated by Xtext 2.18.0.M3
 */
package quasylab.sibilla.lang.pm.model;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Number Of</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link quasylab.sibilla.lang.pm.model.NumberOf#getAgent <em>Agent</em>}</li>
 * </ul>
 *
 * @see quasylab.sibilla.lang.pm.model.ModelPackage#getNumberOf()
 * @model
 * @generated
 */
public interface NumberOf extends Expression
{
  /**
   * Returns the value of the '<em><b>Agent</b></em>' reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Agent</em>' reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Agent</em>' reference.
   * @see #setAgent(Species)
   * @see quasylab.sibilla.lang.pm.model.ModelPackage#getNumberOf_Agent()
   * @model
   * @generated
   */
  Species getAgent();

  /**
   * Sets the value of the '{@link quasylab.sibilla.lang.pm.model.NumberOf#getAgent <em>Agent</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Agent</em>' reference.
   * @see #getAgent()
   * @generated
   */
  void setAgent(Species value);

} // NumberOf
