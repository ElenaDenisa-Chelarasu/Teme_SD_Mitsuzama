package ejb;
import interfaces.BankAccountBeanRemote;
import java.io.Serializable;
public class BankAccountBeanImpl implements BankAccountBeanRemote,
        Serializable {
    private Integer availableAmount = 0;
    public Boolean withdraw(Integer amount) {
        if(availableAmount >= amount) {
            availableAmount -= amount;
            return true;
        } else {
            return false;
        }
    }
    public void deposit(Integer amount) {
        availableAmount += amount;
    }
    public Integer getBalance() {
        return availableAmount;
    }
}