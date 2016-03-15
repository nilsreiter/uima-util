

/* First created by JCasGen Tue Mar 15 14:51:02 CET 2016 */
package de.unistuttgart.ims.uimautil.api;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** 
 * Updated by JCasGen Tue Mar 15 14:51:02 CET 2016
 * XML source: /Users/reiterns/Documents/Workspace/uima-util/src/test/java/desc/type/typesystem.xml
 * @generated */
public class TestType extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(TestType.class);
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int type = typeIndexID;
  /** @generated
   * @return index of the type  
   */
  @Override
  public              int getTypeIndexID() {return typeIndexID;}
 
  /** Never called.  Disable default constructor
   * @generated */
  protected TestType() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public TestType(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public TestType(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public TestType(JCas jcas, int begin, int end) {
    super(jcas);
    setBegin(begin);
    setEnd(end);
    readObject();
  }   

  /** 
   * <!-- begin-user-doc -->
   * Write your own initialization here
   * <!-- end-user-doc -->
   *
   * @generated modifiable 
   */
  private void readObject() {/*default - does nothing empty block */}
     
 
    
  //*--------------*
  //* Feature: MyFeature

  /** getter for MyFeature - gets 
   * @generated
   * @return value of the feature 
   */
  public int getMyFeature() {
    if (TestType_Type.featOkTst && ((TestType_Type)jcasType).casFeat_MyFeature == null)
      jcasType.jcas.throwFeatMissing("MyFeature", "de.unistuttgart.ims.uimautil.api.TestType");
    return jcasType.ll_cas.ll_getIntValue(addr, ((TestType_Type)jcasType).casFeatCode_MyFeature);}
    
  /** setter for MyFeature - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setMyFeature(int v) {
    if (TestType_Type.featOkTst && ((TestType_Type)jcasType).casFeat_MyFeature == null)
      jcasType.jcas.throwFeatMissing("MyFeature", "de.unistuttgart.ims.uimautil.api.TestType");
    jcasType.ll_cas.ll_setIntValue(addr, ((TestType_Type)jcasType).casFeatCode_MyFeature, v);}    
  }

    