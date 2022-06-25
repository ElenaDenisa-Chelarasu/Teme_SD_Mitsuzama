import interfaces.BankAccountBeanRemote;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
public class ProcessBankOperationServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response) throws ServletException, IOException {
// preluare parametri din cererea HTTP
        String operation = request.getParameter("operation");
        String amountString = request.getParameter("amount");
// nu conteaza suma introdusa in campul numeric daca operatiunea este de tip "Sold cont"
        Integer amount = (!amountString.equals("")) ?
                Integer.parseInt(amountString) : 0;
// se incearca preluarea bean-ului folosind obiectul HttpSession, care pastreaza o sesiune HTTP intre client si server
        BankAccountBeanRemote bankAccount;
        bankAccount = (BankAccountBeanRemote)request.getSession().getAttribute("bankAccountBean");
// daca nu exista nimic pastrat in sesiunea HTTP, inseamna ca bean-ul se preia prin JNDI lookup
        if (bankAccount == null) {
            try {
                InitialContext ctx = new InitialContext();
                bankAccount = (BankAccountBeanRemote) ctx.lookup("bankaccount#interfaces.BankAccountBeanRemote");
// dupa preluarea bean-ului prin JNDI, obiectul se stocheaza in sesiune pentru a fi refolosit ulterior
// cererile urmatoare vor utiliza obiectul remote stocat in sesiune
                request.getSession().setAttribute("bankAccountBean", bankAccount);
            } catch (NamingException e) {
                e.printStackTrace();
                return;
            }
        }
        Integer accountBalance = null;
        String message = "";
// in functie de operatia selectata de client, se apeleaza metoda corespunzatoare din obiectul remote
        if (operation.equals("deposit")) {
            bankAccount.deposit(amount);
            message = "In contul dvs. au fost depusa suma: " + amount
                    + ".";
        } else if (operation.equals("withdraw")) {
            if (bankAccount.withdraw(amount)) {
                message = "Din contul dvs. s-a retras suma de: " +
                        amount + ".";
            } else {
                message = "Operatiunea a esuat! Fonduri insuficiente.";
            }
        } else if (operation.equals("balance")) {
            accountBalance = bankAccount.getBalance();
        }
        message += "<br /><br />";
        if (accountBalance != null) {
            message += "Sold cont: " + accountBalance;
        }
        message += "<br /><a href='./'>Inapoi la meniul principal</a>";
        // dupa construirea mesajului raspuns, acesta este trimis ca si continut HTML inapoi la clientul apelant
        response.setContentType("text/html");
        response.getWriter().print(message);
    }
}