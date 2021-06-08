package dao;

/**
 * Created by VHACONMICHEG on 1/24/2017.
 */
public class ConceptDao {

    private static final long serialVersionUID = 1L;

    private String concept;
    private String documentID;
    private String runtype;

    public String getConcept() {
        return concept;
    }

    public void setConcept(String concept) {
        this.concept = concept;
    }

    public String getDocumentID() { return documentID;    }

    public void setDocumentID(String documentID) {
        this.documentID = documentID;
    }

    public String getRuntype()  {return runtype;    }

    public void setRuntype(String runtype)  {this.runtype = runtype;    }
}
