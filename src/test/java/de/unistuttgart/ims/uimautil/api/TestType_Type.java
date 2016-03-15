
/* First created by JCasGen Tue Mar 15 14:51:02 CET 2016 */
package de.unistuttgart.ims.uimautil.api;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.FSGenerator;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.Feature;
import org.apache.uima.jcas.tcas.Annotation_Type;

/** 
 * Updated by JCasGen Tue Mar 15 14:51:02 CET 2016
 * @generated */
public class TestType_Type extends Annotation_Type {
  /** @generated 
   * @return the generator for this type
   */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (TestType_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = TestType_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new TestType(addr, TestType_Type.this);
  			   TestType_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new TestType(addr, TestType_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = TestType.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("de.unistuttgart.ims.uimautil.api.TestType");
 
  /** @generated */
  final Feature casFeat_MyFeature;
  /** @generated */
  final int     casFeatCode_MyFeature;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public int getMyFeature(int addr) {
        if (featOkTst && casFeat_MyFeature == null)
      jcas.throwFeatMissing("MyFeature", "de.unistuttgart.ims.uimautil.api.TestType");
    return ll_cas.ll_getIntValue(addr, casFeatCode_MyFeature);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setMyFeature(int addr, int v) {
        if (featOkTst && casFeat_MyFeature == null)
      jcas.throwFeatMissing("MyFeature", "de.unistuttgart.ims.uimautil.api.TestType");
    ll_cas.ll_setIntValue(addr, casFeatCode_MyFeature, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public TestType_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_MyFeature = jcas.getRequiredFeatureDE(casType, "MyFeature", "uima.cas.Integer", featOkTst);
    casFeatCode_MyFeature  = (null == casFeat_MyFeature) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_MyFeature).getCode();

  }
}



    