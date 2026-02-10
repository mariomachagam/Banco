package bancoservidor;

public class CuentaBancaria {
    private double saldo;
    private String iban;

    public CuentaBancaria(double saldo, String iban) {
        this.saldo = saldo;
        this.iban = iban;
    }

    public double getSaldo() { return saldo; }
    public String getIban() { return iban; }
    public void setSaldo(double saldo) { this.saldo = saldo; }

    public void ingresar(double cantidad) { saldo += cantidad; }
    public boolean retirar(double cantidad) {
        if(cantidad <= saldo) { saldo -= cantidad; return true; }
        return false;
    }
}
