package dds.monedero.model;

import dds.monedero.exceptions.MaximaCantidadDepositosException;
import dds.monedero.exceptions.MaximoExtraccionDiarioException;
import dds.monedero.exceptions.MontoNegativoException;
import dds.monedero.exceptions.SaldoMenorException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class MonederoTest {
  private Cuenta cuenta;
  private Movimiento unMovimiento;

  @BeforeEach
  void init() {
    cuenta = new Cuenta();
  }

  @Test
  void Poner() {
    cuenta.poner(1500);
    assertEquals(1500, cuenta.getSaldo());
  }

  @Test
  void PonerMontoNegativo() {
    assertThrows(MontoNegativoException.class, () -> cuenta.poner(-1500));
  }

  @Test
  void HacerTresDepositos() {
    cuenta.poner(1500);
    cuenta.poner(456);
    cuenta.poner(1900);
    assertEquals(3856, cuenta.getSaldo());
  }

  @Test
  void MasDeTresDepositos() {
    assertThrows(MaximaCantidadDepositosException.class, () -> {
          cuenta.poner(1500);
          cuenta.poner(456);
          cuenta.poner(1900);
          cuenta.poner(245);
    });
  }

  @Test
  void ExtraerMasQueElSaldo() {
    assertThrows(SaldoMenorException.class, () -> {
          cuenta.setSaldo(90);
          cuenta.sacar(1001);
    });
  }

  @Test
  public void ExtraerMasDe1000() {
    assertThrows(MaximoExtraccionDiarioException.class, () -> {
      cuenta.setSaldo(5000);
      cuenta.sacar(1001);
    });
  }

  @Test
  public void ExtraerMontoNegativo() {
    assertThrows(MontoNegativoException.class, () -> cuenta.sacar(-500));
  }

  @Test
  public void ElDineroExtraidoEs100() {
    cuenta.poner(1500);
    cuenta.sacar(10);
    cuenta.sacar(80);
    cuenta.sacar(10);
    assertEquals(100, cuenta.getMontoExtraidoA(LocalDate.now()));
  }

  @Test
  public void ElDineroExtraidoNoEs100(){
    cuenta.poner(1500);
    cuenta.sacar(10);
    cuenta.sacar(100);
    cuenta.sacar(10);
    assertNotEquals(100, cuenta.getMontoExtraidoA(LocalDate.now()));
  }

  @Test
  public void unMovimientoFueDepositado(){
    unMovimiento = new Movimiento(LocalDate.now(), 100, true);
    cuenta.agregarMovimiento(unMovimiento);
    assertTrue(unMovimiento.fueDepositado(LocalDate.now()));
  }

  @Test
  public void setUnMovimientoNoFueDepositado(){
    unMovimiento = new Movimiento(LocalDate.now(), 100, true);
    cuenta.agregarMovimiento(unMovimiento);
    assertFalse(unMovimiento.fueDepositado(LocalDate.ofYearDay(2020, 5)));
  }
}
