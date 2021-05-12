package dds.monedero.model;

import dds.monedero.exceptions.MaximaCantidadDepositosException;
import dds.monedero.exceptions.MaximoExtraccionDiarioException;
import dds.monedero.exceptions.MontoNegativoException;
import dds.monedero.exceptions.SaldoMenorException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Cuenta {

  private double saldo = 0;
  private List<Movimiento> movimientos = new ArrayList<>();

  public void setMovimientos(List<Movimiento> movimientos) {
    this.movimientos = movimientos;
  }

  public void poner(double cuanto) {
    this.validarSiSePuedePoner(cuanto);
    Movimiento movimiento = new Movimiento(LocalDate.now(), cuanto, true);
    this.agregarMovimiento(movimiento);
    this.sumarSaldo(cuanto);
  }

  private void validarSiSePuedePoner(double cuanto) {
    if (cuanto <= 0) {
      throw new MontoNegativoException(cuanto + ": el monto a ingresar debe ser un valor positivo");
    }

    if (getMovimientos().stream().filter(movimiento -> movimiento.isDeposito()).count() >= 3) {
      throw new MaximaCantidadDepositosException("Ya excedio los " + 3 + " depositos diarios");
    }
  }

  public void sacar(double cuanto) {
    this.validarSiSePuedeSacar(cuanto);
    Movimiento movimiento = new Movimiento(LocalDate.now(), cuanto, false);
    this.agregarMovimiento(movimiento);
    this.restarSaldo(cuanto);
  }

  private void validarSiSePuedeSacar(double cuanto) {
    if (cuanto <= 0) {
      throw new MontoNegativoException(cuanto + ": el monto a ingresar debe ser un valor positivo");
    }
    if (getSaldo() - cuanto < 0) {
      throw new SaldoMenorException("No puede sacar mas de " + getSaldo() + " $");
    }
    double montoExtraidoHoy = getMontoExtraidoA(LocalDate.now());
    double limite = 1000 - montoExtraidoHoy;
    if (cuanto > limite) {
      throw new MaximoExtraccionDiarioException("No puede extraer mas de $ " + 1000
          + " diarios, límite: " + limite);
    }
  }

  public void agregarMovimiento(Movimiento unMovimiento) {
    movimientos.add(unMovimiento);
  }

  public double getMontoExtraidoA(LocalDate fecha) {
    return getMovimientos().stream()
        .filter(movimiento -> movimiento.fueExtraido(fecha))
        //reemplaza a !movimiento.isDeposito() && movimiento.getFecha().equals(fecha)
        .mapToDouble(Movimiento::getMonto)
        .sum();
  }

  public void sumarSaldo(double cuanto){
    this.saldo += cuanto;
  }

  public void restarSaldo(double cuanto){
    this.saldo -= cuanto;
  }

  public List<Movimiento> getMovimientos() {
    return movimientos;
  }

  public double getSaldo() {
    return saldo;
  }

  public void setSaldo(double saldo) {
    this.saldo = saldo;
  }

}
