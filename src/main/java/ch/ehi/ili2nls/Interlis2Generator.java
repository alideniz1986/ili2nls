package ch.ehi.ili2nls;


import ch.interlis.ili2c.metamodel.*;

import java.io.File;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;

import ch.ehi.basics.io.IndentPrintWriter;
import ch.ehi.basics.logging.EhiLogger;

/** A class used to generate an INTERLIS model description as INTERLIS-2.
*/
public class Interlis2Generator
{
  IndentPrintWriter   ipw;
  TransferDescription td;
  /** the predefined model INTERLIS
   *
   */
  PredefinedModel               modelInterlis;
  Unit                anyUnit;
  boolean             withPredefined;
  int                 numErrors = 0;
  
private java.util.ArrayList selfStandingConstraints=null;
private ModelElements modelElements;

  public Interlis2Generator()
  {
  }
  public static Interlis2Generator generateElements (
	Writer out, TransferDescription td,ModelElements modelElements)
  {
	Interlis2Generator i = new Interlis2Generator();
	i.setup(out,td,false,modelElements);
	return i;
  }
  static public String debugToString(TransferDescription td,ch.interlis.ili2c.metamodel.Element ele)
  {
	java.io.StringWriter syntaxBuffer=new java.io.StringWriter();
	Interlis2Generator makeSyntax=Interlis2Generator.generateElements(syntaxBuffer,td,null);
	makeSyntax.printElement(ele.getContainer(),null,ele,null,null);
	makeSyntax.ipw.flush();
    return syntaxBuffer.toString();
  }

  private void finish ()
  {
    ipw.close();
  }



  protected void printError ()
  {
    ipw.print (Element.makeErrorName (null));
    numErrors = numErrors + 1;
  }



  /** Generates an INTERLIS-2 model description.


      @param out            A Writer object to which the text will be written.
      @param td             The description to be written.
      @param withPredefined Whether or not to include the predefined <code>MODEL INTERLIS</code>.


      @return The number of errors that have occured while generating, for example because
              an invalid model has been passed. Invalid models can result from parsing a
              file with recoverable syntactic or semantic errors. Of course, the parser reports
              these errors.
  */
  
  public int generate (
	Writer out, TransferDescription td,ModelElements modelEles, String language, String filename)
	{
		return generate(out,td,false,modelEles,language, filename);
	}
  public int generate (
    Writer out, TransferDescription td, boolean withPredefined,ModelElements modelEles, String language, String filename)
  {
	setup(out, td, withPredefined,modelEles);
    printTransferDescription (td, language, filename);
    finish();
    return numErrors;
  }
private void setup(
	Writer out,
	TransferDescription td,
	boolean withPredefined,ModelElements modelEles) {
	ipw = new IndentPrintWriter (out);
	this.td = td;
	modelInterlis = td.INTERLIS;
	anyUnit = td.INTERLIS.ANYUNIT;
	this.withPredefined = withPredefined;
	this.modelElements=modelEles;
}

  private boolean printModifierHelper(boolean first, boolean flag, String what)
  {
    if (flag) {
      if (!first)
        ipw.print(", ");
      ipw.print(what);
      return false;
    } else
      return first;
  }



  protected void printModifiers(boolean _abstract,
    boolean _final, boolean _extended,boolean _ordered, boolean _external,boolean _transient)
  {
    if (!_abstract && !_final && !_extended && !_ordered && !_external && !_transient){
		return;
    }


    boolean first = true;
    ipw.print(" (");
    first = printModifierHelper(first, _abstract, "ABSTRACT");
    first = printModifierHelper(first, _final, "FINAL");
    first = printModifierHelper(first, _extended, "EXTENDED");
	first = printModifierHelper(first, _ordered, "ORDERED");
	first = printModifierHelper(first, _external, "EXTERNAL");
	first = printModifierHelper(first, _transient, "TRANSIENT");
    ipw.print(')');
  }

  private String getTranslatedName(Element el)
  {
	  return null;
	//return modelElements.findModelElement(el.getScopedName()).getName_fr();
  }

  protected void printTopic (Topic topic, String language, String filename)
  {
    if (topic == null)
      return;

	selfStandingConstraints=new java.util.ArrayList();
	
    Topic extending = (Topic) topic.getExtending();

    
	
    ipw.print("TOPIC ");
    if (language == null) {
    	printDocumentation(topic.getDocumentation());
    	ipw.print(topic.getName()); 
    } else {
    	ipw.print(getNameInLanguage(topic, language));
    }
    
    printModifiers(topic.isAbstract(), topic.isFinal(),
      /* EXTENDED */false, /*ORDERED*/false,/*EXTERNAL*/false,/*TRANSIENT*/false);


    if (extending != null)
    {
      ipw.print(" EXTENDS ");
      ipw.print(extending.getScopedName(/* scope */ topic));
    }


    ipw.println(" =");
    ipw.indent();
	
	Domain basketOid=topic.getBasketOid();
	if(basketOid!=null){
		
		ipw.print("BASKET OID AS ");
		ipw.print(basketOid.getScopedName(topic));
		ipw.println(';');
	}
	Domain classOid=topic.getOid();
	if(classOid!=null){
		
		ipw.print("OID AS ");
		ipw.print(classOid.getScopedName(topic));
		ipw.println(';');
	}

    Iterator it = topic.getDependentOn();
    if (it.hasNext())
    {
      ipw.print("DEPENDS ON ");
      ipw.print(((Topic) it.next()).getScopedName(topic));
      while (it.hasNext())
      {
        ipw.print(", ");
        ipw.print(((Topic) it.next()).getScopedName(topic));
      }
      ipw.println(';');
      ipw.println();
    }


    printElements(topic, language, filename);
    Iterator csi=selfStandingConstraints.iterator();
    Viewable view=null;
	Viewable lastView=null;
    while(csi.hasNext()){
    	Constraint cs=(Constraint)csi.next();
    	view=(Viewable)cs.getContainer();
    	if(view!=lastView){
    		if(lastView!=null){
				ipw.unindent();
				ipw.println("END;");
    		}else{
				ipw.println ();
    		}
    		lastView=view;
			ipw.print("CONSTRAINTS OF ");
			ipw.print(view.getName());
			ipw.println('=');
			ipw.indent();
    	}
    	printConstraint(cs);
    }
	if(lastView!=null){
		ipw.unindent();
		ipw.println("END;");
	}
    ipw.unindent();


    /* Stefan Keller <Stefan.Keller@lt.admin.ch> always wants an empty
       line before END Topic -- 1999-10-06/Sascha Brawer
    */
    ipw.println ();
    ipw.print("END ");
    if (language == null) {
    	ipw.print(topic.getName());
    } else {
    	ipw.print(getNameInLanguage(topic, language));
    }
    
    ipw.println(';');
  }



  protected void printAbstractClassDef (AbstractClassDef def, String language, String filename)
  {
	  
	if (language == null) {
		printDocumentation(def.getDocumentation());
	} else {
		printDocumentation(getDocumentationInLanguage(def, language));
	}
	
	printMetaValues(def.getMetaValues());

	String keyword;
	if(def instanceof Table){
		Table tdef=(Table)def;
		if (tdef.isIdentifiable())
		  keyword = "CLASS";
		else
		  keyword = "STRUCTURE";
	}else if(def instanceof AssociationDef){
		keyword = "ASSOCIATION";
	}else{
		throw new IllegalArgumentException();
	}


    printStart (keyword, def, /* based on */ null, language);
    ipw.println (" =");
    ipw.indent ();
    Domain oid=def.getDefinedOid();
    if(oid!=null){
    	if(oid instanceof NoOid){
    	    ipw.println ("NO OID;");
    	}else{
    	    ipw.print ("OID AS ");
    	    ipw.print (oid.getScopedName (def.getContainer()));
    	    ipw.println (";");
    	}
    }
    printElements(def,language,filename);
    printEnd (def, language);
  }

  private void printRenamedViewableRef (Container scope, ViewableAlias ref)
  {
    if (ref == null)
    {
      printError ();
      return;
    }


    String name = ref.getName ();
    Viewable v = ref.getAliasing ();


    if ((name == null) || (v == null))
    {
      printError ();
      return;
    }


    if (!name.equals (v.getName()))
    {
      ipw.print (name);
      ipw.print ('~');
    }
    ipw.print (v.getScopedName (scope));
  }



  protected void printRenamedViewableRefs (Container scope, ViewableAlias[] refs)
  {
    if ((refs == null) || (refs.length == 0))
      return;


    printRenamedViewableRef (scope, refs[0]);
    for (int i = 1; i < refs.length; i++)
    {
      ipw.print (", ");
      printRenamedViewableRef (scope, refs[i]);
    }
  }



  protected void printStart (String keyword, ExtendableContainer ec, Viewable basedOn, String language)
  {
    if (ec == null)
      return;


    ExtendableContainer extending = (ExtendableContainer) ec.getExtending ();
    boolean extendingSameName = false;
    if (extending != null)
    {
      Topic myTopic = (Topic) ec.getContainer (Topic.class);
      Topic extendedTopic = (Topic) extending.getContainer (Topic.class);


      if ((myTopic != null) && (extendedTopic != null)
        && myTopic.isExtending (extendedTopic)
        && ec.getName().equals (extending.getName()))
      {
        extendingSameName = true;
      }
    }


    ipw.print (keyword);
    ipw.print (' ');

    if (language == null) {
    	ipw.print (ec.getName());
    } else {
    	ipw.print(getNameInLanguage(ec, language));
    }
    
    boolean _transient=false;
    if(ec instanceof View){
    	_transient=((View)ec).isTransient();
    }
    printModifiers (ec.isAbstract(), ec.isFinal(), extendingSameName, /*ORDERED*/false,/*EXTERNAL*/false,/*TRANSIENT*/_transient);


    if ((extending != null) && !extendingSameName)
    {
      ipw.print(" EXTENDS ");
      ipw.print(extending.getScopedName(/* scope */ ec.getContainer (ch.interlis.ili2c.metamodel.Element.class)));
    }


    if (basedOn != null)
    {
      ipw.print (" BASED ON ");
      ipw.print (basedOn.getScopedName (/* scope */ ec.getContainer (ch.interlis.ili2c.metamodel.Element.class)));
    }


  }


  protected void printEnd (ExtendableContainer ec, String language)
  {
    if (ec == null)
      return;


    ipw.unindent ();
    ipw.print ("END ");
    if (language == "") {
    	ipw.print (ec.getName ());
    } else {
    	ipw.print (getNameInLanguage(ec,language));
    }
    
    ipw.println (';');
  }



  public void printView (View view, String language, String filename)
  {
	  printView (view,false, language,filename);
  }
  public void printView (View view,boolean suppressDoc, String language, String filename)
  {
	  if(!suppressDoc){
			printDocumentation(view.getDocumentation());
			printMetaValues(view.getMetaValues());
	  }
    printStart ("VIEW", view, /* basedOn */ null, language);
    ipw.println ("");
    ipw.indent ();
    if (view instanceof Projection)
    {
      ipw.print("PROJECTION OF ");
      ipw.print(((Projection) view).getSelected().getAliasing().getScopedName(/*scope*/view));
    }
    else if (view instanceof JoinView)
    {
      ipw.print("JOIN OF ");
      printRenamedViewableRefs (/* scope */ view,
                                ((JoinView) view).getJoining());
    }
    else if (view instanceof UnionView)
    {
      ipw.print("UNION OF ");
      printRenamedViewableRefs (/* scope */ view, 
                                ((UnionView) view).getUnited());
    }
    else if (view instanceof DecompositionView)
    {
      ObjectPath decomposedAttribute;
      Viewable      decomposedViewable = null;


      decomposedAttribute = ((DecompositionView) view).getDecomposedAttribute ();
      if (((DecompositionView) view).isAreaDecomposition())
        ipw.print ("AREA ");
      ipw.print ("DECOMPOSITION OF ");


      if (decomposedAttribute == null)
        printError ();
      else
      {
      	// TODO ce 7.2.2002
        //ViewableAlias alias = decomposedAttribute.getStart();
        //if (alias != null)
        //  decomposedViewable = alias.getAliasing();
        //
        //printRef (/* scope */ view, decomposedViewable);
        //ipw.print (':');
        //printAttributePath (/* scope */ view, decomposedAttribute);
      }
    }
    else {
      printError ();
      ipw.println("<unknown view type>");
    }
    ipw.println ("; =");


    printElements(view,language,filename);
    printEnd (view,language);
  }

  public void printGraphic (Graphic graph, String language, String filename)
  {
	  printGraphic (graph,false, language, filename);
  }
  public void printGraphic (Graphic graph,boolean suppressDoc, String language, String filename)
  {
    if (graph == null)
      return;

    if(!suppressDoc){
    	printDocumentation(graph.getDocumentation());
    	printMetaValues(graph.getMetaValues());
    }
   printStart ("GRAPHIC", graph, /* basedOn */ graph.getBasedOn(), language);
   ipw.println (" =");
   ipw.indent ();
    printElements (graph,language,filename);
    printEnd (graph, language);
  }



  /**
      1: Disjunction
      2: Conjunction
      3: Implication
      4: Negation
      5: Equality, Inequality, LessThanOrEqual,
         GreaterThanOrEqual, LessThan, GreaterThan
      6: ExistenceCheck
      7: DefinedCheck
      8: others (= elements)
  */
  protected int getExpressionPrecedence (Evaluable ev)
  {
    if (ev instanceof Expression.Disjunction)
      return 1;


    if (ev instanceof Expression.Conjunction)
      return 2;


    if (ev instanceof Expression.Negation)
      return 4;


    if ((ev instanceof Expression.Equality) || (ev instanceof Expression.Inequality)
        || (ev instanceof Expression.LessThanOrEqual) || (ev instanceof Expression.LessThan)
        || (ev instanceof Expression.GreaterThanOrEqual) || (ev instanceof Expression.GreaterThan))
      return 5;

    if (ev instanceof Expression.DefinedCheck)
      return 7;


    return 8;
  }



  protected void printExpression (Container scope, Evaluable expr)
  {
    printExpression (scope, expr, 1);
  }



  protected void printExpression (Container scope, Evaluable expr, int precedence)
  {
    int exprPrec = getExpressionPrecedence (expr);
    if (exprPrec < precedence)
    {
      ipw.print ('(');
      printExpression (scope, expr, 1);
      ipw.print (')');
      return;
    }


    if (expr instanceof ObjectPath)
    {
      printAttributePath (scope, (ObjectPath) expr);
      return;
    }



    if (expr instanceof Constant.Undefined)
    {
      ipw.print ("UNDEFINED");
      return;
    }


    if (expr instanceof Constant.Numeric)
    {
      Constant.Numeric cnum = (Constant.Numeric) expr;
      ipw.print (cnum.getValue ());
      if (cnum.getUnit() != null)
      {
        ipw.print ('[');
        printRef (scope, cnum.getUnit());
        ipw.print (']');
      }
      return;
    }


    if (expr instanceof Constant.Text)
    {
      ipw.print ('"');
      ipw.print (((Constant.Text) expr).getValue());
      ipw.print ('"');
      return;
    }

    if (expr instanceof Constant.AttributePath)
    {
      ipw.print (">>");
      ipw.print (((Constant.AttributePath) expr).getValue().getName());
      return;
    }

    if (expr instanceof Constant.Enumeration)
    {
      ipw.print ('#');
      String[] val = ((Constant.Enumeration) expr).getValue ();
      if (val == null)
        printError ();
      else
      {
        for (int i = 0; i < val.length; i++)
        {
          if (i > 0)
            ipw.print ('.');
          if (val[i] == null)
            printError ();
          else
            ipw.print (val[i]);
        }
      }
      return;
    }


    if (expr instanceof Constant.ReferenceToMetaObject)
    {
      MetaObject refMO = ((Constant.ReferenceToMetaObject) expr).getReferred();


      ipw.print ('"');
      if (refMO != null)
        ipw.print (refMO.getName());
      else
        printError ();
      ipw.print ('"');
      return;
    }


    if (expr instanceof Constant.Structured)
    {
      String val = ((Constant.Structured) expr).toString ();
      if (val == null)
        printError ();
      else
        ipw.print (val);
      return;
    }


    if (expr instanceof FunctionCall)
    {
      FunctionCall f = (FunctionCall) expr;
      printRef (scope, f.getFunction());
      ipw.print (" (");
      Evaluable[] args = f.getArguments();
      if (args == null)
        printError ();
      else
        for (int i = 0; i < args.length; i++)
        {
          if (i > 0)
            ipw.print (", ");
          printExpression (scope, args [i]);
        }
      ipw.print (')');
      return;
    }



    if (expr instanceof ParameterValue)
    {
      ipw.print ("PARAMETER ");
      printRef (scope, ((ParameterValue) expr).getParameter ());
      return;
    }



    if (expr instanceof Expression.Disjunction)
    {
      Evaluable[] disjoined = ((Expression.Disjunction) expr).getDisjoined ();
      for (int i = 0; i < disjoined.length; i++)
      {
        if (i > 0)
          ipw.print (" OR ");


        printExpression (scope, disjoined[i], 2);
      }
      return;
    }


    if (expr instanceof Expression.Conjunction)
    {
      Evaluable[] conjoined = ((Expression.Conjunction) expr).getConjoined ();
      for (int i = 0; i < conjoined.length; i++)
      {
        if (i > 0)
          ipw.print (" AND ");


        printExpression (scope, conjoined[i], 3);
      }
      return;
    }



    if (expr instanceof Expression.Negation)
    {
      ipw.print ("NOT (");
      printExpression (scope, ((Expression.Negation) expr).getNegated(), 5);
	  ipw.print (")");
      return;
    }


    if (expr instanceof Expression.Equality)
    {
      printExpression (scope, ((Expression.Equality) expr).getLeft(), 6);
      ipw.print (" == ");
      printExpression (scope, ((Expression.Equality) expr).getRight(), 6);
      return;
    }


    if (expr instanceof Expression.Inequality)
    {
      printExpression (scope, ((Expression.Inequality) expr).getLeft(), 6);
      ipw.print (" <> ");
      printExpression (scope, ((Expression.Inequality) expr).getRight(), 6);
      return;
    }


    if (expr instanceof Expression.LessThanOrEqual)
    {
      printExpression (scope, ((Expression.LessThanOrEqual) expr).getLeft(), 6);
      ipw.print (" <= ");
      printExpression (scope, ((Expression.LessThanOrEqual) expr).getRight(), 6);
      return;
    }


    if (expr instanceof Expression.GreaterThanOrEqual)
    {
      printExpression (scope, ((Expression.GreaterThanOrEqual) expr).getLeft(), 6);
      ipw.print (" >= ");
      printExpression (scope, ((Expression.GreaterThanOrEqual) expr).getRight(), 6);
      return;
    }


    if (expr instanceof Expression.LessThan)
    {
      printExpression (scope, ((Expression.LessThan) expr).getLeft(), 6);
      ipw.print (" < ");
      printExpression (scope, ((Expression.LessThan) expr).getRight(), 6);
      return;
    }


    if (expr instanceof Expression.GreaterThan)
    {
      printExpression (scope, ((Expression.GreaterThan) expr).getLeft(), 6);
      ipw.print (" > ");
      printExpression (scope, ((Expression.GreaterThan) expr).getRight(), 6);
      return;
    }

    if (expr instanceof Expression.DefinedCheck)
    {
      ipw.print ("DEFINED (");
      printExpression (scope, ((Expression.DefinedCheck) expr).getArgument(), 7);
      ipw.print (')');
      return;
    }



    if (expr instanceof ConditionalExpression)
    {
      ConditionalExpression.Condition[] conds = ((ConditionalExpression) expr).getConditions();


      ipw.print ("WITH ");
      printAttributePath (scope, ((ConditionalExpression) expr).getAttribute());
      ipw.println (" (" /*)*/ );
      ipw.indent ();


      if (conds == null)
        printError();
      else
      {
        for (int i = 0; i < conds.length; i++)
        {
          if (i != 0)
            ipw.println (",");


          if (conds[i] == null)
          {
            printError();
            continue;
          }


          printExpression (scope, conds[i].getValue());
          ipw.print (" WHEN IN ");
          printExpression (scope, conds[i].getCondition());
        }
      }


      ipw.unindent ();
      ipw.print (/*(*/ ')');
      return;
    }


    printError ();
  }


/**
 ExistenceConstraint = 'EXISTENCE' 'CONSTRAINT'
                           AttributePath 'REQUIRED' 'IN'
                               ViewableRef ':' AttributePath
                               { 'OR' ViewableRef ':' AttributePath } ';'.
*/
  protected void printExistenceConstraint (Viewable forTable, ExistenceConstraint ec)
  {

      ipw.print ("EXISTENCE CONSTRAINT ");
      ObjectPath attr=ec.getRestrictedAttribute();
      printAttributePath(forTable,attr);
      ipw.print (" REQUIRED IN ");
      Iterator reqi=ec.iteratorRequiredIn();
      String next="";
      while(reqi.hasNext()){
        ObjectPath req=(ObjectPath)reqi.next();
        ipw.print (next);next=" OR ";
        printRef(forTable,req.getRoot());
        ipw.print(":");
        printAttributePath(forTable,req);
      }
    ipw.println (';');
  }
  protected void printSetConstraint (Viewable forTable, SetConstraint ec)
  {

      ipw.print ("SET CONSTRAINT");
      if(ec.getPreCondition()!=null){
          ipw.print (" WHERE ");
          printExpression (forTable, ec.getPreCondition());
          ipw.println(": ");
      }else{
          ipw.println("");
      }
      ipw.indent();
      printExpression (forTable, ec.getCondition());
      ipw.println (';');
      ipw.unindent();
  }

  protected void printUniquenessConstraint (Viewable forTable, UniquenessConstraint uc)
  {
    UniqueEl uel = uc.getElements();
    Iterator pathi=uel.iteratorAttribute();
    if(uc.getLocal()){
      ipw.print ("UNIQUE (LOCAL) ");
      ObjectPath prefix=uc.getPrefix();
      printAttributePath(forTable,prefix);
      String next=": ";
      while(pathi.hasNext()){
        ObjectPath path=(ObjectPath)pathi.next();
        ipw.print (next);next=", ";
        printAttributePath(forTable,path);
      }
    }else{
      ipw.print ("UNIQUE");
      String next=" ";
      while(pathi.hasNext()){
        ObjectPath path=(ObjectPath)pathi.next();
        ipw.print (next);next=", ";
        printAttributePath(forTable,path);
      }
    }
    ipw.println (';');
  /*
    UniqueEl[] uel = uc.getElements();
    AttributeRef[] prefix = uc.getPrefix();


    ipw.print ("UNIQUE ");


    if(prefix!=null && prefix.length > 0)
    {
      printAttributeRefs (prefix);
      ipw.print (':');
    }
    for (int i = 0; i < uel.length; i++)
    {
      if (i > 0)
        ipw.print (", ");
      if (uel[i] == null){
        printError ();
      }else{
        ; // TODO printAttributePath(forTable,uel[i].getAttribute());
      }
    }
    ipw.println (';');
    */
  }

  public void printConstraint(Constraint elt)
  {
	  printConstraint(elt,false);  
  }
  public void printConstraint(Constraint elt,boolean suppressDoc)
  {
	  if(suppressDoc){
			printDocumentation(elt.getDocumentation());
			printMetaValues(elt.getMetaValues());
	  }
      Container container=elt.getContainer();
      if (elt instanceof MandatoryConstraint)
      {
        ipw.println("MANDATORY CONSTRAINT");
        ipw.indent();
        printExpression (container, ((MandatoryConstraint) elt).getCondition());
        ipw.println (';');
        ipw.unindent();
      }
      else if (elt instanceof PlausibilityConstraint)
      {
        PlausibilityConstraint pc = (PlausibilityConstraint) elt;
        ipw.print ("CONSTRAINT ");
        if (pc.getDirection() == PlausibilityConstraint.DIRECTION_AT_LEAST)
          ipw.print (" >= ");
        else
          ipw.print (" <= ");
        ipw.print (pc.getPercentage());
        ipw.println ('%');
        ipw.indent();
        printExpression (container, pc.getCondition());
        ipw.println (';');
        ipw.unindent();
      }
      else if (elt instanceof UniquenessConstraint)
      {
        UniquenessConstraint uc = (UniquenessConstraint) elt;
        printUniquenessConstraint ((Viewable) container, uc);
      }
      else if (elt instanceof ExistenceConstraint)
      {
        printExistenceConstraint((Viewable) container, (ExistenceConstraint) elt);
      }
      else if (elt instanceof SetConstraint)
      {
        printSetConstraint((Viewable) container, (SetConstraint) elt);
      }
  }
  public void printGraphicParameterDef(GraphicParameterDef gfxp,String language)
  {
	  printGraphicParameterDef(gfxp,false,language);
  }
  public void printGraphicParameterDef(GraphicParameterDef gfxp,boolean suppressDoc, String language)
  {
	  if(!suppressDoc){
			printDocumentation(gfxp.getDocumentation());
			printMetaValues(gfxp.getMetaValues());
	  }
	  String scopedNamePrefix = gfxp.getScopedName();
    ipw.print(gfxp.getName());
    ipw.print(" : ");
    printType(gfxp.getContainer(),gfxp.getDomain(),language, scopedNamePrefix);
    ipw.println(";");
  }
  public void printMetaDataUseDef(MetaDataUseDef mu)
  {
	  printMetaDataUseDef(mu,false);
  }
  public void printMetaDataUseDef(MetaDataUseDef mu,boolean suppressDoc)
  {
	  if(!suppressDoc){
			printDocumentation(mu.getDocumentation());
			printMetaValues(mu.getMetaValues());
	  }
    if(mu.isSignData()){
      ipw.print("SIGN BASKET ");
    }else{
      ipw.print("REFSYSTEM BASKET ");
    }

    ipw.print(mu.getName());

    printModifiers(/*ABSTRACT*/false, mu.isFinal(),/*EXTENDED*/false, /*ORDERED*/false,/*EXTERNAL*/false,/*TRANSIENT*/false);

    /* TODO ce 2002-05-07 handle EXTENDS in MetaDataUseDef
    MetaDataUseDef extending = (MetaDataUseDef) mu.getExtending();
    if (extending != null)
    {
      ipw.print(" EXTENDS ");
      ipw.print(extending.getScopedName(mu.getContainer()));
    }
    */
    Topic topic=mu.getTopic();
    ipw.print("~");
    ipw.print(topic.getContainer().getName());
    ipw.print(".");
    ipw.print(topic.getName());
    ipw.indent();
    ipw.indent();
    Table lastTable=null;
    String sep="";
    for(Iterator moi=mu.iterator();moi.hasNext();){
    	Object moo=moi.next();
    	if(moo instanceof MetaObject){
    		MetaObject mo=(MetaObject)moo;
    	    Table table=mo.getTable();
    	    if(table!=lastTable){
       	    	ipw.println();
    	        ipw.unindent();
    	    	ipw.print("OBJECTS OF ");
    	    	ipw.print(table.getName());
    	    	ipw.println(":");
    	        ipw.indent();
    	    	lastTable=table;
    	    }else{
    	    	ipw.println(",");
    	    }
	    	printDocumentation(mo.getDocumentation());
	    	printMetaValues(mo.getMetaValues());
	    	ipw.print(mo.getName());
    	}
    }
    ipw.println(";");
    ipw.unindent();
    ipw.unindent();

  }


  public void printUnit (Container scope, Unit u, String language)
  {
	  printUnit (scope, u,false,language);
  }
  public void printUnit (Container scope, Unit u,boolean suppressDoc,String language)
  {
    if (u == null)
    {
      printError ();
      return;
    }


    Unit extending = (Unit)u.getExtending();

    if(!suppressDoc){
    	if (language == null) {
    		printDocumentation(u.getDocumentation());
    	} else {
    		printDocumentation(getDocumentationInLanguage(u, language));
    	}
    	
    	printMetaValues(u.getMetaValues());
    }
    ipw.print(u.getDocName());
    if (!u.getDocName().equals(u.getName())) {
      ipw.print(" [");
      if (language == null) {
    	  ipw.print(u.getName());
      } else {
  		String value = getNameInLanguage(u, language);
  		ipw.print(value);
      }
      
      ipw.print(']');
    }


    printModifiers (u.isAbstract(), /* FINAL */ false, /* EXTENDED */ false, /*ORDERED*/false,/*EXTERNAL*/false,/*TRANSIENT*/false);


    if ((extending != null) && (extending != anyUnit) && (!(u instanceof DerivedUnit)))
    {
      ipw.print(" EXTENDS ");
      ipw.print(extending.getScopedName(scope));
    }


    if (u instanceof NumericallyDerivedUnit)
    {
      NumericallyDerivedUnit.Factor[] factors;


      factors = ((NumericallyDerivedUnit) u).getConversionFactors();
      ipw.print(" =");


      if (factors.length >= 1)
      {
        for (int i = 0; i < factors.length; i++)
        {
          if (i > 0)
          {
            ipw.print (' ');
            ipw.print (factors[i].getConversionOperator());
          }
          ipw.print (' ');
          printNumericConst (factors[i].getConversionFactor());
        }
      }


      ipw.print (" [");
      printRef (scope, ((NumericallyDerivedUnit) u).getExtending());
      ipw.print (']');
    }
    else if (u instanceof FunctionallyDerivedUnit)
    {
      ipw.print (" = FUNCTION ");
      printExplanation (((FunctionallyDerivedUnit) u).getExplanation ());
      ipw.print (" [");
      printRef (scope, ((FunctionallyDerivedUnit) u).getExtending());
      ipw.print (']');
    }
    else if (u instanceof ComposedUnit)
    {
      ComposedUnit.Composed[] composed;


      composed = ((ComposedUnit) u).getComposedUnits();
      ipw.print(" = (");
      for (int i = 0; i < composed.length; i++)
      {
      	if(i>0){
	        ipw.print (' ');
	        ipw.print (composed[i].getCompositionOperator());
	        ipw.print (' ');
		}
        printRef (scope, composed[i].getUnit());
      }
      ipw.print(')');
    }
    else if (u instanceof StructuredUnit)
    {
      StructuredUnit.Part[] parts;

      parts = ((StructuredUnit) u).getParts();
      ipw.print (" = {");
      printRef (scope, ((StructuredUnit) u).getFirstUnit());
      for (int i = 0; i < parts.length; i++)
      {
        ipw.print (':');
        printRef (scope, parts[i].getUnit ());
        ipw.print ('[');
        ipw.print (parts[i].getMinimum ());
        ipw.print ("..");
        ipw.print (parts[i].getMaximum ());
        ipw.print (']');
      }
      ipw.print ('}');


      if (((StructuredUnit) u).isContinuous())
        ipw.print (" CONTINUOUS");
    }

    if (u instanceof StructuredUnit){
        EhiLogger.logError("UNIT "+u.getName()+": StructuredUnit not supported by INTERLIS 2.3");
        ipw.println("; !! Hint: comment out/remove");
    }else{
        ipw.println(';');
    }

  }



  protected void printRef (Container scope, Element elt)
  {
    if (elt == null){
      printError ();
    }else if(elt==modelInterlis.ANYCLASS){
		ipw.print ("ANYCLASS");
    }else if(elt==modelInterlis.ANYSTRUCTURE){
        ipw.print ("ANYSTRUCTURE");
    }else{
      ipw.print (elt.getScopedName (scope));
    }
  }



  public void printParameter (Container scope, Parameter par,String language)
  {
	  printParameter (scope, par,false,language);
  }
  public void printParameter (Container scope, Parameter par,boolean suppressDoc,String language)
  {
	  String scopedNamePrefix = "";
    if (par == null)
    {
      printError ();
      return;
    }


    if(!suppressDoc){
    	printDocumentation(par.getDocumentation());
    	printMetaValues(par.getMetaValues());
    }
    ipw.print(par.getName());


    Parameter ext = par.getExtending ();
    if (ext != null)
    {
      if (par.getName().equals (ext.getName()))
        ipw.print (" (EXTENDED)");
      else
      {
        ipw.print (" EXTENDS ");
        printRef (scope, ext);
      }
    }


    ipw.print(": ");


    Type typ = par.getType();
    if (typ instanceof ReferenceType)
    {
      ipw.print ("-> ");
      printRef (scope, ((ReferenceType) typ).getReferred());
    }
    else
      scopedNamePrefix = typ.getScopedName();
      printType (scope, typ,language,scopedNamePrefix);
    ipw.println(';');
  }



  private void printNumericConst (PrecisionDecimal num)
  {
    if (num == PrecisionDecimal.PI)
    {
      ipw.print("PI");
      return;
    }
    if (num == PrecisionDecimal.LNBASE)
    {
      ipw.print("LNBASE");
      return;
    }
    ipw.print(num.toString());
  }


  protected void printRoleDef(Container scope,
								RoleDef role)
  {

	printDocumentation(role.getDocumentation());
	printMetaValues(role.getMetaValues());
	ipw.print(role.getName());
	printModifiers(role.isAbstract(), role.isFinal(),
	  role.isExtended(),role.isOrdered(),role.isExternal(),/*TRANSIENT*/false);
	String kind="";
	switch(role.getKind()){
	  case RoleDef.Kind.eASSOCIATE:
		kind=" -- ";
		break;
	  case RoleDef.Kind.eAGGREGATE:
	  	kind=" -<> ";
		break;
	  case RoleDef.Kind.eCOMPOSITE:
	  	kind=" -<#> ";
		break;
	}
	ipw.print(kind);
	if(role.getDefinedCardinality()!=null){
	  ipw.print(role.getDefinedCardinality()+" ");
	}
	printRef (scope, role.getDestination());
	Iterator resti=role.getReference().iteratorRestrictedTo();
	String sep=" RESTRICTION (";
	boolean hasRestriction=false;
	while(resti.hasNext()){
		AbstractClassDef rest=(AbstractClassDef)resti.next();
		ipw.print(sep);sep=";";
		printRef (scope, rest);
		hasRestriction=true;
	}
	if(hasRestriction){
		ipw.print(")");
	}
	ipw.println(';');
		
  }
  protected void printAttribute(Container scope,
                                AttributeDef attrib, String language)
  {
    if (attrib == null)
    {
      printError ();
      return;
    }
	if(attrib instanceof LocalAttribute){
		LocalAttribute la=(LocalAttribute)attrib;
		if(la.isGeneratedByAllOf()){
			return;
		}
		
	}
	Type proxyType=attrib.getDomain();
	if(proxyType!=null && (proxyType instanceof ObjectType)){
		if(((ObjectType)proxyType).isAllOf()){
	        ipw.println("ALL OF "+attrib.getName()+";");
		}else{
			// skip implicit particles (base-viewables) of views
		}
		return;
	}
	if (language == null) {
		printDocumentation(attrib.getDocumentation());
	} else {
		String value = getDocumentationInLanguage(attrib, language);
		printDocumentation(value);
	}
	//printDocumentation(attrib.getDocumentation());
	printMetaValues(attrib.getMetaValues());

	if(attrib instanceof LocalAttribute){
		LocalAttribute la=(LocalAttribute)attrib;
		if(la.isSubdivision()){
			if(la.isContinuous()){
				ipw.print("CONTINUOUS ");
			}
			ipw.print("SUBDIVISION ");
		}
	}
	if (language == null) {
		ipw.print(attrib.getName());
	} else {
		String value = getNameInLanguage(attrib, language);
		ipw.print(value);
	}
	
    //ipw.print(attrib.getName());
    printModifiers(attrib.isAbstract(), attrib.isFinal(),
      /* EXTENDED */ attrib.getExtending() != null, /*ORDERED*/false,/*EXTERNAL*/false,/*TRANSIENT*/attrib.isTransient());


    if (attrib instanceof LocalAttribute){
      if(attrib.getDomain()==null && scope instanceof View){
    	  // skip typ if attribute inside ViewDef
      }else{
          ipw.print(" : ");

          String scopedNamePrefix = attrib.getScopedName();
          printType (scope, attrib.getDomain(), language, scopedNamePrefix);
      }
      printAttributeBasePath(scope, attrib);
    }
    if(attrib instanceof LocalAttribute && attrib.getDomain() instanceof StructuredUnitType){
        EhiLogger.logError("ATTRIBUTE "+attrib.getScopedName(null)+": StructuredUnitType not supported by INTERLIS 2.3; replace by TextType or FormattedType/XMLDate");
        ipw.println("; !! Hint: replace by TextType or FormattedType/XMLDate");
    }else{
        ipw.println(';');
    }
  }
public void printAttributeBasePath(Container scope, AttributeDef attrib) {
	Evaluable[] paths = ((LocalAttribute) attrib).getBasePaths ();
      if ((paths == null) || (paths.length == 0)){
        ; // nothing
      }else{
        ipw.print (" := ");
        for (int i = 0; i < paths.length; i++)
        {
          if (i > 0)
            ipw.print (", ");
          printExpression(scope, paths[i]);
        }
      }
}



  protected void printSignAttribute (Graphic scope,
                                     SignAttribute attrib)
  {
    SignAttribute extending = (SignAttribute)attrib.getExtending();


	printDocumentation(attrib.getDocumentation());
    ipw.print(attrib.getName());
    printModifiers(/* ABSTRACT */ false, /* FINAL */ false,
      /* EXTENDED */ extending != null, /*ORDERED*/false,/*EXTERNAL*/false,/*TRANSIENT*/false);


    if ((extending == null)
        || (attrib.getGenerating() != extending.getGenerating()))
    {
      ipw.print (" OF ");
      printRef (scope, attrib.getGenerating());
    }


    ipw.println(":");
    ipw.indent ();


    SignInstruction[] instructions = attrib.getInstructions ();
    for (int i = 0; i < instructions.length; i++)
    {
      if (i > 0)
        ipw.println (',');
      printSignInstruction (scope.getBasedOn(), instructions[i]);
    }


    ipw.unindent ();
    ipw.println(';');
  }



  protected void printSignInstruction (Viewable basedOn, SignInstruction instr)
  {
    if (instr == null)
    {
      printError ();
      return;
    }


    Evaluable restrictor = instr.getRestrictor ();
    if (restrictor != null)
    {
      ipw.print ("WHERE ");
      printExpression (basedOn, restrictor);
      ipw.println ();
    }
    ipw.println ('(');
    ipw.indent ();


    ParameterAssignment[] assignments = instr.getAssignments();
    for (int i = 0; i < assignments.length; i++)
    {
      if (i > 0)
        ipw.println (';');
      printParameterAssignment (basedOn, assignments[i]);
    }


    ipw.unindent ();
    ipw.println ();
    ipw.print (')');
  }



  protected void printParameterAssignment (Viewable basedOn, ParameterAssignment parass)
  {
    if (parass == null)
    {
      printError ();
      return;
    }


    Parameter assigned = parass.getAssigned();
    if (assigned == null)
      printError ();
    else
      ipw.print (assigned.getName ());


    ipw.print (" := ");
    printExpression (basedOn, parass.getValue());
  }


  public void printMetaValues(ch.ehi.basics.settings.Settings values)
  {
	  if(values!=null){
		  for(Iterator valuei=values.getValues().iterator();valuei.hasNext();){
			  String name=(String)valuei.next();
			  String value=values.getValue(name);
			  ipw.print("!!@ ");
			  ipw.print(name);
			  ipw.print("=");
			  if(value.indexOf(' ')!=-1 || value.indexOf('=')!=-1 || value.indexOf(';')!=-1 || value.indexOf(',')!=-1 || value.indexOf('"')!=-1 || value.indexOf('\\')!=-1){
				  ipw.println("\""+value+"\"");
			  }else{
				  ipw.println(value);
			  }
		  }
	  }
  }
  public void printDocumentation(String doc)
	{
	if(doc==null)return;
	if(doc.length()==0)return;
	String beg="/** ";
	// for each line
	int last=0;
	int next=doc.indexOf("\n",last);
	while(next>-1){
	  String line=doc.substring(last,next);
	  ipw.println(beg+line);
	  beg=" * ";
	  last=next+1;
	  next=doc.indexOf("\n",last);
	}
	  String line= "";

	  line=doc.substring(last);
	  String newLine = "";
	  newLine = beg + line;
	  //ipw.println(beg+line);
	  ipw.println(newLine);
	  ipw.println(" */");
	}

  protected void printModel (Model mdef, String language, String filename)
  {
	File fileNameFromMdef = new File(mdef.getFileName());
	File fileNameFromIli = new File(filename); 
	if (!fileNameFromMdef.getName().equals(fileNameFromIli.getName())) {
		return;
	}
	  
	if (language == null) {
		printDocumentation(mdef.getDocumentation());
	} else {
		String value = getDocumentationInLanguage(mdef, language);
		printDocumentation(value);
	}
	
	printMetaValues(mdef.getMetaValues());
	
	if(mdef.isContracted()){
		ipw.print("CONTRACTED ");
	}
	
	if(language == null) {
		ipw.print("MODEL " + mdef.getName());
	}else {
		String value = getNameInLanguage(mdef, language);
		ipw.print("MODEL " + value);
	}
    
	if(language == null) {
		if(mdef.getLanguage()!=null){ 
			ipw.print("("+mdef.getLanguage()+")");
		} 
	} else {
		ipw.print("("+language+")");
	}
    
	ipw.println ();
	ipw.indent();
	String issuer=mdef.getIssuer();
	if(issuer==null){
		issuer="mailto:"+System.getProperty("user.name")+"@localhost";
	}
    ipw.println("AT \""+issuer+"\"");
    String version=mdef.getModelVersion();
    if(version==null){
		java.util.Calendar current=java.util.Calendar.getInstance();
		java.text.DecimalFormat digit4 = new java.text.DecimalFormat("0000");
		java.text.DecimalFormat digit2 = new java.text.DecimalFormat("00");
		version=digit4.format(current.get(java.util.Calendar.YEAR))
			+"-"+digit2.format(current.get(java.util.Calendar.MONTH)+1)
			+"-"+digit2.format(current.get(java.util.Calendar.DAY_OF_MONTH));
    }
    ipw.print("VERSION \""+version+"\"");
	String expl=mdef.getModelVersionExpl();
	if ((expl != null) && (expl.length() > 0))
	{
	  ipw.print (' ');
	  printExplanation (expl);
	}
	// TODO Translation
	Element modelInRootLanguage = Ili2TranslationXml.getElementInRootLanguage(mdef);
	if (modelInRootLanguage.getScopedName() != null) {
		String translationText = "TRANSLATION OF " + modelInRootLanguage.getScopedName() + "[\""+((Model) modelInRootLanguage).getModelVersion()+"\"]";
		ipw.println(translationText);
	}
	ipw.println(" =");
    //ipw.println ();

    Importable[] imported = mdef.getImporting();

    String sep="";
    boolean modelsImported=false;
    for (int i = 0; i < imported.length; i++)
    {

      Importable curImport = (Importable) imported[i];

      if (curImport instanceof Model){
      	if(curImport!=modelInterlis){
      		if(!modelsImported){
      		    ipw.println("IMPORTS");
      		    ipw.indent();
      		    modelsImported=true;
      		}
          	ipw.print(sep+((Model) curImport).getName());
          	sep=", ";
      	}
      }else{
        printError ();
      }
    }
	if(modelsImported){
	    ipw.println(';');
	    ipw.unindent();
	    ipw.println();
	}

    printElements (mdef,language,filename);

    ipw.unindent();
    ipw.println ();
    ipw.print ("END ");
    if (language == null) {
    	ipw.print (mdef.getName());
    } else {
    	ipw.print(getNameInLanguage(mdef, language));
    }
    
    ipw.println ('.');
    ipw.println ();
    
  }

private String getDocumentationInLanguage(Element ele, String language) {
	String modelName = "";
	Iterator<ModelElement> iteratorModelElement = modelElements.iterator();
	
	while (iteratorModelElement.hasNext()) { 
		ModelElement element = iteratorModelElement.next();
		if (Ili2TranslationXml.getElementInRootLanguage(ele).getScopedName().equals(element.getScopedName())) {
			if (language.equals("fr")) {
				modelName = element.getDocumentation_fr();
			} else if (language.equals("en")) {
				modelName = element.getDocumentation_en();
			} else if (language.equals("it")) {
				modelName = element.getDocumentation_it();
			} else if (language.equals("rm")) {
				modelName = element.getDocumentation_rm();
			} else if (language.equals("de")) {
				modelName = element.getDocumentation_de();
			}	
		}			
	}
	if (modelName == null) {
		return "";
	} else return modelName;
}
private String getNameInLanguage(Element ele, String language) {
	String modelName = "";
	Iterator<ModelElement> iteratorModelElement = modelElements.iterator();
	
	while (iteratorModelElement.hasNext()) { 
		ModelElement element = iteratorModelElement.next();
		if (Ili2TranslationXml.getElementInRootLanguage(ele).getScopedName().equals(element.getScopedName())) {
			if (language.equals("fr")) {
				modelName = element.getName_fr();
			} else if (language.equals("en")) {
				modelName = element.getName_en();
			} else if (language.equals("it")) {
				modelName = element.getName_it();
			} else if (language.equals("rm")) {
				modelName = element.getName_rm();
			} else if (language.equals("de")) {
				modelName = element.getName_de();
			}	
		}			
	}
	if (modelName == null) {
		return "";
	} else return modelName;
}
protected void printExplanation(String explanationText)
  {
    ipw.print("//");
    ipw.print(explanationText);
    ipw.print("//");
  }



  protected void printDomainDef (Container scope, Domain dd, String language)
  {
    Domain extending = dd.getExtending();
    String scopedNamePrefix = "";

	printDocumentation(dd.getDocumentation());
	printMetaValues(dd.getMetaValues());
    ipw.print (dd.getName());
    if(dd.getType() instanceof TypeAlias && ((TypeAlias)dd.getType()).getAliasing()==td.INTERLIS.INTERLIS_1_DATE){
    	Domain dd2=((TypeAlias)dd.getType()).getAliasing();
        printModifiers (dd2.isAbstract(), dd2.isFinal(),
      	      /* EXTENDED */ false, /*ORDERED*/false,/*EXTERNAL*/false,/*TRANSIENT*/false);
	    ipw.print(" = ");
	    printType (scope, dd2.getType(), language, scopedNamePrefix);
        ipw.println(';');
    }else{
        printModifiers (dd.isAbstract(), dd.isFinal(),
        	      /* EXTENDED */ false, /*ORDERED*/false,/*EXTERNAL*/false,/*TRANSIENT*/false);


        	    if (extending != null)
        	    {
        	      ipw.print(" EXTENDS ");
        	      printRef (scope, extending);
        	    }


        	    ipw.print(" = ");
        	    scopedNamePrefix = dd.getScopedName();
        	    printType (scope, dd.getType(),language,scopedNamePrefix);
        	    if(dd.getType() instanceof StructuredUnitType){
        	        EhiLogger.logError("DOMAIN "+dd.getName()+": StructuredUnitType not supported by INTERLIS 2.3; replace by TextType or FormattedType/XMLDate");
        	        ipw.println("; !! Hint: replace by TextType or FormattedType/XMLDate");
        	    }else{
        	        ipw.println(';');
        	    }
    }
  }



  public void printReferenceSysRef (Container scope, RefSystemRef rsr)
  {
    if (rsr == null)
    {
      printError ();
      return;
    }


    if (rsr instanceof RefSystemRef.CoordSystem)
    {
      RefSystemRef.CoordSystem cs = (RefSystemRef.CoordSystem) rsr;
      ipw.print ('{');
      printRef (scope, cs.getSystem ());
      ipw.print ('}');
    }
    else if (rsr instanceof RefSystemRef.CoordSystemAxis)
    {
      RefSystemRef.CoordSystemAxis csa = (RefSystemRef.CoordSystemAxis) rsr;
      ipw.print ('{');
      printRef (scope, csa.getSystem ());
      ipw.print ('[');
      ipw.print (csa.getAxisNumber ());
      ipw.print (']');
      ipw.print ('}');
    }
    else if (rsr instanceof RefSystemRef.CoordDomain)
    {
      RefSystemRef.CoordDomain cda = (RefSystemRef.CoordDomain) rsr;
      ipw.print ('<');
      printRef (scope, cda.getReferredDomain ());
      ipw.print ('>');
    }
    else if (rsr instanceof RefSystemRef.CoordDomainAxis)
    {
      RefSystemRef.CoordDomainAxis cda = (RefSystemRef.CoordDomainAxis) rsr;
      ipw.print ('<');
      printRef (scope, cda.getReferredDomain ());
      ipw.print ('[');
      ipw.print (cda.getAxisNumber ());
      ipw.print (']');
      ipw.print ('>');
    }
    else
    {
      printError ();
    }
  }



  public void printType (Container scope, Type dd, String language, String scopedNamePrefix)
  {
    if (dd == null)
    {
      printError ();
      return;
    }

    if (dd.isMandatory() && !(dd instanceof CompositionType))
      ipw.print("MANDATORY ");


    if (dd instanceof NumericalType)
      printNumericalType (scope, (NumericalType) dd);
    else if (dd instanceof TextType)
    {
      int len = ((TextType) dd).getMaxLength();
      if(((TextType) dd).isNormalized()){
          ipw.print("TEXT");
      }else{
          ipw.print("MTEXT");
      }


      if (len != -1) {
        ipw.print('*');
        ipw.print(len);
      }
    }
    else if (dd instanceof FormattedType)
    {
        FormattedType ft = (FormattedType) dd;
        if(ft.getDefinedBaseStruct()!=null){
        	ipw.print("FORMAT BASED ON ");
            printRef (scope, ft.getDefinedBaseStruct());
            Iterator baseAttri=ft.iteratorDefinedBaseAttrRef();
            if(baseAttri.hasNext()){
            	ipw.print(" (");
            	String sep="";
            	if(ft.getExtending()!=null){
            		ipw.print("INHERITANCE");
            		sep=" ";
            	}
            	if(ft.getDefinedPrefix()!=null){
            		ipw.print("\""+ft.getDefinedPrefix()+"\"");
            		sep=" ";
            	}
            	while(baseAttri.hasNext()){
                	ipw.print(sep);
                	FormattedTypeBaseAttrRef baseAttr=(FormattedTypeBaseAttrRef)baseAttri.next();
                	if(baseAttr.getFormatted()!=null){
                    	ipw.print(baseAttr.getAttr().getName());
                    	ipw.print("/");
                    	printRef(scope,baseAttr.getFormatted());
                	}else{
                    	ipw.print(baseAttr.getAttr().getName());
                    	if(baseAttr.getIntPos()!=0){
                        	ipw.print("/");
                    		ipw.print(baseAttr.getIntPos());
                    	}
                	}
                	if(baseAttr.getPostfix()!=null){
                		ipw.print(" \""+baseAttr.getPostfix()+"\"");
                	}
            		sep=" ";
            	}
            	ipw.print(")");
            }
        	if(ft.getDefinedMinimum()!=null){
            	ipw.print(" ");
                printFormatedTypeMinMax(ft);
        	}
        }else if(ft.getDefinedBaseDomain()!=null){
        	ipw.print("FORMAT ");
            printRef (scope, ft.getDefinedBaseDomain());
        	ipw.print(" ");
            printFormatedTypeMinMax(ft);
        }else{
            printFormatedTypeMinMax(ft);
        }
    }
    else if (dd instanceof EnumerationType)
    {
      EnumerationType et = (EnumerationType) dd;
      //String scopedNamePrefix = scope.getScopedName();

      printEnumeration(et.getEnumeration(), language, scopedNamePrefix);
      if (et.isCircular())
        ipw.print(" CIRCULAR");
      else if (et.isOrdered())
        ipw.print(" ORDERED");
    }
    else if (dd instanceof EnumTreeValueType)
    {
      EnumTreeValueType et = (EnumTreeValueType) dd;
      ipw.print("ALL OF ");
      printRef (scope, et.getEnumType());
    }  
    else if (dd instanceof EnumValType)
    {
        if(((EnumValType) dd).isOnlyLeafs()){
            ipw.print("ENUMVAL");
        }else{
            ipw.print("ENUMTREEVAL");
        }
    }  
    else if (dd instanceof TypeAlias){
      Domain def=((TypeAlias) dd).getAliasing();
      if(def==modelInterlis.BOOLEAN){
        ipw.print("BOOLEAN");
      }else if(def==modelInterlis.VALIGNMENT){
        ipw.print("VALIGNMENT");
      }else if(def==modelInterlis.HALIGNMENT){
        ipw.print("HALIGNMENT");
      }else{
        printRef (scope, ((TypeAlias) dd).getAliasing());
      }
    }else if (dd instanceof CompositionType)
    {
      CompositionType comp = (CompositionType) dd;
      Cardinality card = comp.getCardinality();
      if (card.getMaximum() == 1)
      {
        /* [MANDATORY] OBJECT */
        if (card.getMinimum() == 1)
          ipw.print("MANDATORY ");
      }
      else
      {
        /* BAG OR LIST */
        ipw.print(comp.isOrdered() ? "LIST " : "BAG ");
	      ipw.print(card);
	      ipw.print(' ');
		ipw.print("OF ");
      }


      printRef (scope, comp.getComponentType());
    }
    else if (dd instanceof ReferenceType)
    {
			ReferenceType ref = (ReferenceType) dd;
			ipw.print("REFERENCE TO ");
			if (ref.isExternal()) {
				ipw.print("(EXTERNAL) ");
			}
			printRef(scope, ref.getReferred());
			Iterator resti = ref.iteratorRestrictedTo();
			String sep = " RESTRICTION (";
			boolean hasRestriction = false;
			while (resti.hasNext()) {
				AbstractClassDef rest = (AbstractClassDef) resti.next();
				ipw.print(sep);
				sep = ";";
				printRef(scope, rest);
				hasRestriction = true;
			}
			if (hasRestriction) {
				ipw.print(")");
			}
    }
    else if (dd instanceof AbstractCoordType)
    {
      NumericalType[] nts = ((AbstractCoordType) dd).getDimensions();
      int nullAxis = ((AbstractCoordType) dd).getNullAxis();
      int piHalfAxis = ((AbstractCoordType) dd).getPiHalfAxis();

      if(dd instanceof MultiCoordType){
          ipw.print ("MULTICOORD ");
      }else{
          ipw.print ("COORD ");
      }
      ipw.indent ();
      for (int i = 0; i < nts.length; i++)
      {
        if (i > 0)
          ipw.print (", ");
        printNumericalType (scope, nts[i]);
      }
      if (nullAxis != 0)
      {
        ipw.println (',');
        ipw.print ("ROTATION ");
        ipw.print (nullAxis);
        ipw.print (" -> ");
        if (piHalfAxis > 0)
          ipw.print (piHalfAxis);
        else
          printError ();
      }
      ipw.unindent ();
    }
    else if (dd instanceof LineType)
    {
      LineType lt = (LineType) dd;
      if (lt instanceof PolylineType){
        ipw.print (((PolylineType) lt).isDirected() ? "DIRECTED POLYLINE" : "POLYLINE");
      }else if (lt instanceof MultiPolylineType){
          ipw.print (((PolylineType) lt).isDirected() ? "DIRECTED MULTIPOLYLINE" : "MULTIPOLYLINE");
      }else if (lt instanceof SurfaceType){
        ipw.print ("SURFACE");
      }else if (lt instanceof MultiSurfaceType){
        ipw.print ("MULTISURFACE");
      }else if (lt instanceof AreaType){
        ipw.print ("AREA");
      }else if (lt instanceof MultiAreaType){
          ipw.print ("MULTIAREA");
      }else{
        printError ();
      }


      LineForm[] lineForms = lt.getDefinedLineForms ();
      PrecisionDecimal maxOverlap = lt.getDefinedMaxOverlap ();
      Domain controlPointDomain = lt.getDefinedControlPointDomain ();
      Table lineAttributeStructure = null;


      if (lt instanceof SurfaceOrAreaType){
        lineAttributeStructure = ((SurfaceOrAreaType) lt).getLineAttributeStructure ();
      }else if(lt instanceof MultiSurfaceOrAreaType){
          lineAttributeStructure = ((MultiSurfaceOrAreaType) lt).getLineAttributeStructure ();
      }

      ipw.indent ();
      boolean needNewLine = false;


      if (lineForms.length > 0)
      {
        if (needNewLine)
          ipw.println ();


        ipw.print (" WITH (");
        for (int i = 0; i < lineForms.length; i++)
        {
          if (i > 0)
            ipw.print (", ");
          ipw.print (lineForms[i].getName());
        }
        ipw.print (')');
        needNewLine = true;
      }


      if (controlPointDomain != null)
      {
        ipw.print (" VERTEX ");
        printRef (scope, controlPointDomain);
        needNewLine = true;
      }


      if (maxOverlap != null)
      {
        if (needNewLine)
          ipw.println ();


        ipw.print (" WITHOUT OVERLAPS > ");
        ipw.print (maxOverlap.toString());
      }



      if (lineAttributeStructure != null)
      {
        ipw.println ();
        ipw.print ("LINE ATTRIBUTES ");
        printRef (scope, lineAttributeStructure);
      }


      ipw.unindent ();
    }else if(dd instanceof OIDType){
      Type type=((OIDType)dd).getOIDType();
      if(type==null){
        ipw.print ("OID ANY");
      }else{
        ipw.print ("OID ");
        scopedNamePrefix = dd.getScopedName();
        printType(scope,type,language, scopedNamePrefix);
      }
	}else if(dd instanceof BlackboxType){
	  BlackboxType bt=(BlackboxType)dd;
	  ipw.print ("BLACKBOX");
	  int kind=bt.getKind();
	  if(kind==BlackboxType.eXML){
		ipw.print (" XML");
	  }else if(kind==BlackboxType.eBINARY){
		ipw.print (" BINARY");
	  }
    }else if(dd instanceof BasketType){
      BasketType bt=(BasketType)dd;
      ipw.print ("BASKET");
      int kind=bt.getKind();
      if(kind==Properties.eDATA){
        ipw.print (" (DATA)");
      }else if(kind==Properties.eVIEW){
        ipw.print (" (VIEW)");
      }else if(kind==Properties.eBASE){
        ipw.print (" (BASE)");
      }else if(kind==Properties.eGRAPHIC){
        ipw.print (" (GRAPHIC)");
      }
      Topic spec=bt.getTopic();
      if(spec!=null){
        ipw.print (" OF ");
        printRef(scope,spec);
      }
    }else if(dd instanceof ClassType){
      ClassType ct=(ClassType)dd;
      if(ct.isStructure()){
        ipw.print ("STRUCTURE");
      }else{
        ipw.print ("CLASS");
      }
      String next=" RESTRICTED TO ";
      Iterator resti=ct.iteratorRestrictedTo();
      while(resti.hasNext()){
        Table rest=(Table)resti.next();
        ipw.print(next);
        printRef(scope,rest);
        next=" ,";
      }
    }else if(dd instanceof AttributePathType){
    	AttributePathType ct=(AttributePathType)dd;
        ipw.print ("ATTRIBUTE");
        FormalArgument argRestr=ct.getArgRestriction();
        ObjectPath attrRestr=ct.getAttrRestriction();
        if(argRestr!=null){
            ipw.print (" OF @ ");
        	ipw.print(argRestr.getName());
        }else if(attrRestr!=null){
            ipw.print (" OF ");
        	printAttributePath(scope,attrRestr);
        }
        Type[] typeRestr=ct.getTypeRestriction();
        if(typeRestr!=null){
            ipw.print (" RESTRICTION ( ");
        	String sep="";
        	for(int typei=0;typei<typeRestr.length;typei++){
                ipw.print (sep);
        		printType(scope,typeRestr[typei], language,scopedNamePrefix);
        		sep=";";
        	}
            ipw.print (" )");
        }
    }else if(dd instanceof ObjectType){
      ObjectType ot=(ObjectType)dd;
      if(ot.isObjects()){
          ipw.print ("OBJECTS OF ");
      }else{
          ipw.print ("OBJECT OF ");
      }
      Viewable ref=ot.getRef();
      printRef(scope,ref);
    }else if(dd instanceof MetaobjectType){
      MetaobjectType ot=(MetaobjectType)dd;
      ipw.print ("METAOBJECT");
      Table ref=ot.getReferred();
      if(ref!=scope){
        ipw.print (" OF ");
        printRef(scope,ref);
      }
    }
  }
private void printFormatedTypeMinMax(FormattedType ft) {
	ipw.print("\"");
	ipw.print(ft.getDefinedMinimum());
	ipw.print("\" .. \"");
	ipw.print(ft.getDefinedMaximum());
	ipw.print("\"");
}



  /** Prints a numerical type (either a NumericType or a StructuredUnitType).
  */
  protected void printNumericalType (Container scope, NumericalType type)
  {
    if (type == null)
    {
      printError ();
      return;
    }


    if (type instanceof NumericType)
    {
      NumericType ntyp = (NumericType) type;
      PrecisionDecimal min = ntyp.getMinimum();
      PrecisionDecimal max = ntyp.getMaximum();
      if (min == null)
        ipw.print("NUMERIC");
      else
      {
        ipw.print(min.toString());
        ipw.print(" .. ");
        ipw.print(max.toString());
      }
    }
    else if (type instanceof StructuredUnitType)
    {
      ipw.print (((StructuredUnitType) type).getMinimum().toString ());
      ipw.print (" .. ");
      ipw.print (((StructuredUnitType) type).getMaximum().toString ());
    }


    if (type.isCircular())
        ipw.print(" CIRCULAR");


    if (type.getUnit() != null)
    {
      ipw.print (" [");
      ipw.print (type.getUnit().getScopedName(scope));
      ipw.print (']');
    }


    switch (type.getRotation())
    {
    case NumericType.ROTATION_COUNTERCLOCKWISE:
      ipw.print(" COUNTERCLOCKWISE");
      break;


    case NumericType.ROTATION_CLOCKWISE:
      ipw.print(" CLOCKWISE");
      break;
    }


    if (type.getReferenceSystem() != null)
    {
      ipw.print (' ');
      printReferenceSysRef (scope, type.getReferenceSystem ());
    }
  }



  protected void printEnumeration (ch.interlis.ili2c.metamodel.Enumeration enumer, String language, String scopedNamePrefix)
  {
    ipw.println ('(');
    ipw.indent ();


    if (enumer == null)
      printError ();
    else
    {
      Iterator iter = enumer.getElements();
      while (iter.hasNext()) {
        printEnumerationElement((ch.interlis.ili2c.metamodel.Enumeration.Element) iter.next(), language, scopedNamePrefix);
        if (iter.hasNext())
          ipw.println (',');
      }
    }


    ipw.unindent ();
    ipw.print (')');
  }



  protected void printEnumerationElement (ch.interlis.ili2c.metamodel.Enumeration.Element ee, String language, String scopedNamePrefix)
  {
	String scopedName = scopedNamePrefix + "." + ee.getName();
	if (language == null) {
		printDocumentation(ee.getDocumentation());
		printMetaValues(ee.getMetaValues());
	    ipw.print(ee.getName());
	} else {
		//String value = getDocumentationInLanguage(ee, language);
		//printDocumentation(value);
		String docu = getEnumerationElementDocumentationInLanguage(scopedName, language);
		String name = getEnumerationElementNameInLanguage(scopedName, language);
		printDocumentation(docu);

		printMetaValues(ee.getMetaValues());
	    ipw.print(name);
	}
	
	//printMetaValues(ee.getMetaValues());
    //ipw.print(ee.getName());


    ch.interlis.ili2c.metamodel.Enumeration subEnum = ee.getSubEnumeration();
    if (subEnum != null)
    {
      ipw.print(' ');
      printEnumeration(subEnum, language, scopedName); 
    }
  }

  private String getEnumerationElementNameInLanguage(String scopedNamePrefix, String language) {
		String modelName = "";
		Iterator<ModelElement> iteratorModelElement = modelElements.iterator();
		
		while (iteratorModelElement.hasNext()) { 
			ModelElement element = iteratorModelElement.next();
			if (scopedNamePrefix.equals(element.getScopedName())) {
				if (language.equals("fr")) {
					modelName = element.getName_fr();
				} else if (language.equals("en")) {
					modelName = element.getName_en();
				} else if (language.equals("it")) {
					modelName = element.getName_it();
				} else if (language.equals("rm")) {
					modelName = element.getName_rm();
				} else if (language.equals("de")) {
					modelName = element.getName_de();
				}	
			}			
		}
		if (modelName == null) {
			return "";
		} else return modelName;
}
private String getEnumerationElementDocumentationInLanguage(String scopedNamePrefix, String language) {
	String modelName = "";
	Iterator<ModelElement> iteratorModelElement = modelElements.iterator();
	
	while (iteratorModelElement.hasNext()) { 
		ModelElement element = iteratorModelElement.next();
		if (scopedNamePrefix.equals(element.getScopedName())) {
			if (language.equals("fr")) {
				modelName = element.getDocumentation_fr();
			} else if (language.equals("en")) {
				modelName = element.getDocumentation_en();
			} else if (language.equals("it")) {
				modelName = element.getDocumentation_it();
			} else if (language.equals("rm")) {
				modelName = element.getDocumentation_rm();
			} else if (language.equals("de")) {
				modelName = element.getDocumentation_de();
			}	
		}			
	}
	if (modelName == null) {
		return "";
	} else return modelName;
}
public void printLineFormTypeDef (Container scope, LineForm lf)
  {
    if (lf == null)
    {
      printError ();
      return;
    }


	printDocumentation(lf.getDocumentation());
	printMetaValues(lf.getMetaValues());
    ipw.print (lf.getName ());


    String explanation = lf.getExplanation();
    if (explanation != null)
    {
      ipw.print (' ');
      printExplanation(explanation);
    }


    ipw.println (';');
  }



  public void printFunctionDeclaration(Container scope, Function f,String language)
  {
	  printFunctionDeclaration(scope,f,false,language);
  }
  public void printFunctionDeclaration(Container scope, Function f,boolean suppressDoc,String language)
  {
    if (f == null) {
      printError ();
      return;
    }


    if(!suppressDoc){
    	printDocumentation(f.getDocumentation());
    	printMetaValues(f.getMetaValues());
    }
    ipw.print("FUNCTION ");
    ipw.print(f.getName());
    ipw.print("(");


    FormalArgument[] args = f.getArguments ();
    if (args == null)
      printError ();
    else
    {
    	String sep=" ";
      for (int i = 0; i < args.length; i++)
      {
      	ipw.print ( sep+args[i].getName()+" : ");
        printType (scope, args[i].getType(),language,scope.getScopedName());
        sep="; ";
      }
    }


    ipw.print(") : ");
    printType (scope, f.getDomain(),language, scope.getScopedName());


    String explanation = f.getExplanation();
    if (explanation != null)
      printExplanation (explanation);


    ipw.println(';');
  }



  protected void printAttributeRefs (AttributeRef[] refs)
  {
    if (refs == null)
    {
      printError ();
      return;
    }


    for (int i = 0; i < refs.length; i++)
    {
      if (i > 0)
        ipw.print ('.');


      if (refs[i] == null)
        printError ();
      else
        ipw.print (refs[i].getName ());
    }
  }



  protected void printAttributePath (Container scope, ObjectPath path)
  {
    if (path == null)
    {
      printError ();
      return;
    }
    PathEl[] elv=path.getPathElements();
    String sep="";
    for(int i=0;i<elv.length;i++){
	ipw.print(sep);sep="->";
	ipw.print(elv[i].getName());
    }
  }


  protected void printElements (Container container, String language, String filename) 
  {
    Class lastClass = null;
    Iterator it = container.iterator();
    
    while (it.hasNext()) {
    	
    	Element element = (Element) it.next();
    	
//    	if (((Model) element.getContainer(Model.class)) != null) {
//    		if (!((Model) element.getContainer(Model.class)).getFileName().contains(filename)) {
//    			continue;
//    		}
//    	}
    	lastClass = printElement(container, lastClass, element, language, filename);
    } 
  }

protected Class printElement(Container container, Class lastClass, ch.interlis.ili2c.metamodel.Element elt, String language, String filename) {	
	if (elt instanceof AttributeDef)
      {
		printAttribute (container, (AttributeDef) elt, language);
        lastClass = AttributeDef.class; 
      }
	  else if (elt instanceof RoleDef)
	  {
		printRoleDef(container, (RoleDef) elt);
		lastClass = RoleDef.class;
	  }
      else if (elt instanceof Function)
      {
        if ((lastClass != null) && (lastClass != Function.class))
          ipw.println();

        printFunctionDeclaration (container, (Function) elt, language);
        lastClass = Function.class;
      }
      else if (elt instanceof Parameter)
      {
        if (lastClass != Parameter.class)
        {
          ipw.println ("PARAMETER");
        }

        printParameter (container, (Parameter) elt, language);
        lastClass = Parameter.class;
      }
      else if (elt instanceof Domain)
      {
        if (lastClass != Domain.class)
        {
          if (lastClass != null)
            ipw.println();
          ipw.println("DOMAIN");
        }

        ipw.indent();
        printDomainDef (container, (Domain) elt, language);
        ipw.unindent();
        lastClass = Domain.class;
      }
      else if (elt instanceof LineForm)
      {
        if (lastClass != LineForm.class)
        {
          if (lastClass != null)
            ipw.println();
          ipw.println ("LINE FORM");
        }

        ipw.indent ();
        printLineFormTypeDef (container, (LineForm) elt);
        ipw.unindent ();
        lastClass = LineForm.class;
      }
      else if (elt instanceof Unit)
      {
        if (lastClass != Unit.class) {
          if (lastClass != null)
            ipw.println();
          ipw.println("UNIT");
        }

        ipw.indent();
        printUnit(container, (Unit) elt,language);
        ipw.unindent();
        lastClass = Unit.class;
      }
      else if (elt instanceof Model)
      {
        if (withPredefined || !(elt instanceof PredefinedModel))
        {
          /* Stefan Keller <Stefan.Keller@lt.admin.ch> always wants
             an empty line before models -- 1999-10-06/Sascha Brawer


           if (lastClass != null)
            ipw.println();


          */
          ipw.println ();
          printModel((Model) elt, language, filename);
          lastClass = Model.class;
        }
      }
      else if (elt instanceof Topic)
      {
        ipw.println ();
        ipw.println ();
        printTopic((Topic) elt, language, filename);
        lastClass = Topic.class;
      }
      else if (elt instanceof MetaDataUseDef)
      {
        ipw.println ();
        printMetaDataUseDef((MetaDataUseDef) elt);
        lastClass = MetaDataUseDef.class;
      }
      else if (elt instanceof Table)
      {
        /* Only explicit tables are printed out.
           Line attribute tables, for example,
           are not printed out.
        */

        if (!((Table) elt).isImplicit ())
        {
          ipw.println ();
          printAbstractClassDef((Table) elt, language, filename);
          lastClass = AbstractClassDef.class;
        }
      }
	  else if (elt instanceof AssociationDef)
	  {
		  ipw.println ();
		  printAbstractClassDef((AssociationDef) elt, language, filename);
		  lastClass = AbstractClassDef.class;
	  }
      else if (elt instanceof View)
      {
        if (lastClass != null)
          ipw.println();
        printView((View) elt, language, filename);
        lastClass = View.class;
      }
      else if (elt instanceof Graphic)
      {
        if (lastClass != null)
          ipw.println();
        printGraphic ((Graphic) elt, language, filename);
        lastClass = Graphic.class;
      }
      else if (elt instanceof Constraint)
      {
		if(((Constraint)elt).isSelfStanding()){
			selfStandingConstraints.add(elt);
		}else{
			printConstraint((Constraint)elt);
			lastClass = Constraint.class;
		}
      }
      else if (elt instanceof ExpressionSelection)
      {
        if (lastClass != null)
          ipw.println();
        ipw.println("WHERE");
        ipw.indent();
        printExpression (((ExpressionSelection) elt).getSelected(),
                         ((ExpressionSelection) elt).getCondition());
        ipw.println (';');
        ipw.unindent();
        lastClass = ExpressionSelection.class;
      }
      else if (elt instanceof SignAttribute)
      {
        if (lastClass != SignAttribute.class)
        {
          if (lastClass != null)
            ipw.println();
        }
        printSignAttribute ((Graphic) container, (SignAttribute) elt);
        lastClass = SignAttribute.class;
      }
	return lastClass;
}



  protected void printTransferDescription (
    TransferDescription   td, String language, String filename)
  {
    ipw.println("INTERLIS 2.3;");
    ipw.unindent();
    ipw.println();


    printElements(td, language, filename);
  }
}

