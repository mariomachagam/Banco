package bancoservidor;

public class Cliente {

    private String usuario;
    private String passwordHash;
    private CuentaBancaria cuenta;

    public Cliente() {
    }

    public Cliente(String usuario, String passwordHash, CuentaBancaria cuenta) {
        this.usuario = usuario;
        this.passwordHash = passwordHash;
        this.cuenta = cuenta;
    }

    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public CuentaBancaria getCuenta() { return cuenta; }
    public void setCuenta(CuentaBancaria cuenta) { this.cuenta = cuenta; }
}
