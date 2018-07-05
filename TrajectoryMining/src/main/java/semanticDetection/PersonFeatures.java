package semanticDetection;

import java.util.List;

/**
 * 人员地理位置和语义位置信息集合
 */
public class PersonFeatures {
    List<PersonFeature> personFeatures;

    public PersonFeatures() {
    }

    public List<PersonFeature> getPersonFeatures() {
        return personFeatures;
    }

    public void setPersonFeatures(List<PersonFeature> personFeatures) {
        this.personFeatures = personFeatures;
    }

    public PersonFeatures(List<PersonFeature> personFeatures) {
        this.personFeatures = personFeatures;

    }
}
