package ejb;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
@Entity
public class StudentEntity {
    @Id
    @GeneratedValue
    private int id;
    private String nume;
    private String prenume;
    private int varsta;
    public StudentEntity() {
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getNume() {
        return nume;
    }
    public void setNume(String nume) {
        this.nume = nume;
    }
    public String getPrenume() {
        return prenume;
    }
    public void setPrenume(String prenume) {
        this.prenume = prenume;
    }
    public int getVarsta() {
        return varsta;
    }
    public void setVarsta(int varsta) {
        this.varsta = varsta;
    }
}