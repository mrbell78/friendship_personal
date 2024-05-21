package ngo.friendship.satellite.model;

import java.io.Serializable;

public class DiagnosisInfo implements Serializable{

    // Column(name = "DIAG_ID")
    private long diagId;
    // Column(name = "CODE_ICD10")
    private String codeIcd10;
    // Column(name = "CODE_SNOMED")
    private String codeSnomed;
    // Column(name = "DIAG_NAME")
    private String diagName;
    // Column(name = "STATE")
    private long state;
    // Column(name = "VERSION_NO")
    private long versionNo;

    public long getDiagId() {
        return diagId;
    }

    public void setDiagId(long diagId) {
        this.diagId = diagId;
    }

    public String getCodeIcd10() {
        return codeIcd10;
    }

    public void setCodeIcd10(String codeIcd10) {
        this.codeIcd10 = codeIcd10;
    }

    public String getCodeSnomed() {
        return codeSnomed;
    }

    public void setCodeSnomed(String codeSnomed) {
        this.codeSnomed = codeSnomed;
    }

    public String getDiagName() {
        return diagName;
    }

    public void setDiagName(String diagName) {
        this.diagName = diagName;
    }

    public long getState() {
        return state;
    }

    public void setState(long state) {
        this.state = state;
    }

    public long getVersionNo() {
        return versionNo;
    }

    public void setVersionNo(long versionNo) {
        this.versionNo = versionNo;
    }



    public static final String MODEL_NAME = "diagnosis_info";


}
