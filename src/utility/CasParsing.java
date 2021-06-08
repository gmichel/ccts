package utility;


//import org.apache.ctakes.typesystem.type.syntax.WordToken;
import org.apache.ctakes.typesystem.type.refsem.Entity;
import org.apache.ctakes.typesystem.type.refsem.OntologyConcept;
import org.apache.ctakes.typesystem.type.syntax.NP;
import org.apache.ctakes.typesystem.type.syntax.WordToken;
import org.apache.ctakes.typesystem.type.textsem.EntityMention;
import org.apache.uima.UIMAFramework;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.cas.*;
import org.apache.uima.cas.impl.LowLevelCAS;
import org.apache.uima.cas.text.AnnotationFS;
import org.apache.uima.cas.text.AnnotationIndex;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.cas.FSList;
import org.apache.uima.jcas.cas.NonEmptyFSList;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.FsIndexDescription;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.apache.uima.tools.util.htmlview.AnnotationViewGenerator;
import org.apache.uima.util.CasCreationUtils;
import org.apache.uima.util.InvalidXMLException;
import org.apache.uima.util.XMLInputSource;
import org.apache.uima.util.XmlCasDeserializer;

import org.xml.sax.SAXException;

import java.io.*;
import java.sql.*;
import java.util.*;
import java.util.zip.GZIPInputStream;

public class CasParsing {

    DBConnection dbConnection = new DBConnection();
    SqlQueryResource sqlQueryResource = new SqlQueryResource();

   // File descriptorFile = new File("TypeSystem.xml");
 //  File descriptorFile = new File("D:\\apache-ctakes-4.0.0\\desc\\ctakes-ytex-uima\\desc\\analysis_engine\\AggregatePlaintextUMLSProcessor.xml");
   File descriptorFile = new File("D:\\apache-ctakes-4.0.0\\desc\\ctakes-ytex-uima\\desc\\analysis_engine\\AggregatePlaintextUMLSProcessor.xml");
    Object descriptor;
    CAS casDescriptor = null;
    {
        try {
            descriptor = UIMAFramework.getXMLParser().parse(new XMLInputSource(descriptorFile));
            casDescriptor = CasCreationUtils.createCas((AnalysisEngineDescription) descriptor);
          //  TypeSystemDescription tsDesc = (TypeSystemDescription) descriptor;
          //  tsDesc.resolveImports();
          //  casDescriptor = CasCreationUtils.createCas(tsDesc, null, new FsIndexDescription[0]);
        } catch (InvalidXMLException e) {
            e.printStackTrace();
        } catch (ResourceInitializationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private CAS loadDocumentCas(Integer documentID)
             {
        ResultSet rs = null;
        GZIPInputStream gzIS = null;
        CAS cas = null;
        try {
         cas = CasCreationUtils.createCas(Collections.EMPTY_LIST,
                 casDescriptor.getTypeSystem(),
                UIMAFramework.getDefaultPerformanceTuningProperties());

             String insertModel = sqlQueryResource.getSqlQuery("getCAS");
            PreparedStatement ps = dbConnection.getDataBaseConnection().prepareStatement(insertModel);
            ps.setInt(1,documentID );

            rs = ps.executeQuery();
            if (rs.next()) {
                gzIS = new GZIPInputStream(new BufferedInputStream(
                        rs.getBinaryStream(1)));
                XmlCasDeserializer.deserialize(gzIS, cas, true);
            } else {
                throw new RuntimeException("No document with id = "
                        + documentID);
            }
            ps.close();
            rs.close();
            gzIS.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (ResourceInitializationException e) {
            e.printStackTrace();
        }
                 return cas;
    }


    public static void main(String[] args)
    {
        CasParsing cp = new CasParsing();
        CAS cas = cp.loadDocumentCas(8480);
    //    System.out.println("sofa"+cas.getSofa().getSofaURI());     //getDocumentAnnotation().getCoveredText());
        //get annotation iterator for this CAS
        FSIndex anIndex = cas.getAnnotationIndex();
        FSIterator anIter = anIndex.iterator();
        while (anIter.isValid()) {
            AnnotationFS annot = (AnnotationFS) anIter.get();
            System.out.println(" " + annot.getType().getName()
                    + ": " + annot.getCoveredText());
            anIter.moveToNext();
        }

        JCas jcas = null;
        try {
            jcas = cas.getJCas();
        } catch (CASException e) {
            e.printStackTrace();
        }

        AnnotationIndex<Annotation> annoIdx = jcas
                .getAnnotationIndex(Annotation.typeIndexID);
        List<Annotation> listAnno = new ArrayList<Annotation>(annoIdx.size());
        for (Annotation annotation : listAnno) {
            System.out.println(annotation);
        }

        Type topType = cas.getTypeSystem().getTopType();
        FSIterator<FeatureStructure> allIndexedFS = cas.getIndexRepository().getAllIndexedFS(topType);

        Iterator<Annotation> wordTokenItr = jcas.getJFSIndexRepository().getAnnotationIndex(WordToken.type).iterator();
String pos = "";
        while (wordTokenItr.hasNext()) {
            WordToken token = (WordToken) wordTokenItr.next();
            String tok = token.getCoveredText();
           pos= token.getPartOfSpeech();
          //  System.out.println(tok);
        }

        Iterator<Annotation> NPItr = jcas.getJFSIndexRepository().getAnnotationIndex(NP.type).iterator();

        while (NPItr.hasNext()) {
            NP np = (NP) NPItr.next();
            String tok = np.getCoveredText();
            System.out.println(tok);
        }



        Iterator<Annotation> EntityMentionItr = jcas.getJFSIndexRepository().getAnnotationIndex(EntityMention.type).iterator();

        while (EntityMentionItr.hasNext()) {
            EntityMention em = (EntityMention) EntityMentionItr.next();
         //   String tok = em.getCoveredText();

            FSArray fsa = em.getOntologyConceptArr();


            for (int i=0; i<fsa.size();i++) {
                FeatureStructure fest = fsa.get(i);
OntologyConcept ont = em.getOntologyConceptArr(i);
             //   System.out.println(fest.);
                System.out.println(ont.getCode());


                List<?> plist = fest.getType().getFeatures();
                for (Object obj : plist) {
                    Feature feature = (Feature) obj;
                    fest.getFeatureValueAsString(feature);
                    System.out.println(fsa.get(i));
                }

            }
           // String[] sss = fsa.toStringArray();
          //  System.out.println(sss[0]);
            //OntologyConcept ont = ent.getOntologyConcept();
            //System.out.println("CODE="+ont.getCode());

          //  System.out.println(tok);
        }

        while (allIndexedFS.hasNext()) {


        //    System.out.println(allIndexedFS.next());
        }

        //System.out.println(cas.getDocumentText());
    }







}
