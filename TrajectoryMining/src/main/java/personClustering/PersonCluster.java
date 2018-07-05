package personClustering;

import java.util.Vector;

/**
 * Created by Silocean on 2016-12-24.
 */
public class PersonCluster {
    Vector<Person> persons = new Vector<>();
    String clusterName;

    public Vector<Person> getPersons() {
        return persons;
    }

    public void setPersons(Vector<Person> persons) {
        this.persons = persons;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }
}
