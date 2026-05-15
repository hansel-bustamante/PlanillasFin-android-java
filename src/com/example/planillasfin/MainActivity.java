package com.example.planillasfin;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class MainActivity extends Activity {

	private EditText etCarnet;
	private Spinner spinnerMes;
	private Button btnBuscar;
	private TextView tvResultado;
	private ArrayList<String[]> bonosAgregados = new ArrayList<String[]>();
	private ArrayList<String[]> descuentosAgregados = new ArrayList<String[]>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		etCarnet = (EditText) findViewById(R.id.etCarnet);
		spinnerMes = (Spinner) findViewById(R.id.spinnerMes);
		btnBuscar = (Button) findViewById(R.id.btnBuscar);
		tvResultado = (TextView) findViewById(R.id.tvResultado);

		Button btnAgregarBono = (Button) findViewById(R.id.btnAgregarBono);
		Button btnAgregarDescuento = (Button) findViewById(R.id.btnAgregarDescuento);

		final String[] meses = { "Marzo", "Abril", "Mayo" };
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, meses);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerMes.setAdapter(adapter);

		btnBuscar.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				buscarEmpleado();
			}
		});

		btnAgregarBono.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				agregarBono();
			}
		});

		btnAgregarDescuento.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				agregarDescuento();
			}
		});
	}

	private void buscarEmpleado() {
		String carnet = etCarnet.getText().toString();
		String mes = spinnerMes.getSelectedItem().toString();

		if (carnet.isEmpty()) {
			Toast.makeText(this, "Ingrese número de carnet", Toast.LENGTH_SHORT)
					.show();
			return;
		}

		String nombreCompleto = buscarNombre(carnet);
		double basico = buscarBasico(carnet, mes);
		double bono = buscarBono(carnet, mes);
		double descuento = buscarDescuento(carnet, mes);
		double total = basico + bono - descuento;

		String resultado = "Nombre: " + nombreCompleto + "\nBásico: " + basico
				+ "\nBonos: " + bono + "\nDescuentos: " + descuento
				+ "\nTotal Ganado: " + total;

		tvResultado.setText(resultado);
	}

	private String buscarNombre(String carnet) {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					getAssets().open("personal.csv")));
			String line;
			reader.readLine(); // Saltar encabezado
			while ((line = reader.readLine()) != null) {
				String[] datos = line.split(";");
				if (datos.length >= 4 && datos[0].equals(carnet)) {
					return datos[1] + " " + datos[2] + " " + datos[3];
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "No encontrado";
	}

	private double buscarBasico(String carnet, String mes) {
		String archivo = mes.equals("Marzo") ? "pla0324.csv" : mes
				.equals("Abril") ? "pla0424.csv" : "pla0524.csv";
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					getAssets().open(archivo)));
			String line;
			reader.readLine(); // Saltar encabezado
			while ((line = reader.readLine()) != null) {
				String[] datos = line.split(";");
				if (datos.length >= 3 && datos[1].equals(carnet)) {
					String cargoId = datos[2];
					return buscarBasicoPorCargo(cargoId);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	private double buscarBasicoPorCargo(String cargoId) {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					getAssets().open("cargos.csv")));
			String line;
			reader.readLine(); // Saltar encabezado
			while ((line = reader.readLine()) != null) {
				String[] datos = line.split(";");
				if (datos.length >= 3 && datos[0].equals(cargoId)) {
					return Double.parseDouble(datos[2]);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	private double buscarBono(String carnet, String mes) {
		double bono = 0;
		String archivo = mes.equals("Marzo") ? "bon0324.csv" : mes
				.equals("Abril") ? "bon0424.csv" : "bon0524.csv";
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					getAssets().open(archivo)));
			String line;
			reader.readLine(); // Saltar encabezado
			while ((line = reader.readLine()) != null) {
				String[] datos = line.split(";");
				if (datos.length >= 3 && datos[1].equals(carnet)) {
					bono += Double.parseDouble(datos[2]);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		for (String[] b : bonosAgregados) {
			if (b[0].equals(carnet)) {
				bono += Double.parseDouble(b[1]);
			}
		}
		return bono;
	}

	private double buscarDescuento(String carnet, String mes) {
		double descuento = 0;
		String archivo = mes.equals("Marzo") ? "des0324.csv" : mes
				.equals("Abril") ? "des0424.csv" : "des0524.csv";
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					getAssets().open(archivo)));
			String line;
			reader.readLine(); // Saltar encabezado
			while ((line = reader.readLine()) != null) {
				String[] datos = line.split(";");
				if (datos.length >= 3 && datos[1].equals(carnet)) {
					descuento += Double.parseDouble(datos[2]);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		for (String[] d : descuentosAgregados) {
			if (d[0].equals(carnet)) {
				descuento += Double.parseDouble(d[1]);
			}
		}
		return descuento;
	}

	private void agregarBono() {
		String carnet = etCarnet.getText().toString();
		if (carnet.isEmpty()) {
			Toast.makeText(this, "Ingrese número de carnet", Toast.LENGTH_SHORT)
					.show();
			return;
		}

		pedirMonto("Ingrese monto del Bono:", carnet, true);
	}

	private void agregarDescuento() {
		String carnet = etCarnet.getText().toString();
		if (carnet.isEmpty()) {
			Toast.makeText(this, "Ingrese número de carnet", Toast.LENGTH_SHORT)
					.show();
			return;
		}

		pedirMonto("Ingrese monto del Descuento:", carnet, false);
	}

	private void pedirMonto(String titulo, final String carnet,
			final boolean esBono) {
		final EditText input = new EditText(this);
		input.setInputType(android.text.InputType.TYPE_CLASS_NUMBER
				| android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(titulo);
		builder.setView(input);

		builder.setPositiveButton("Aceptar",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						String montoStr = input.getText().toString();
						if (montoStr.isEmpty()) {
							Toast.makeText(MainActivity.this, "Monto inválido",
									Toast.LENGTH_SHORT).show();
							return;
						}
						if (esBono) {
							bonosAgregados
									.add(new String[] { carnet, montoStr });
							Toast.makeText(MainActivity.this,
									"Bono agregado correctamente",
									Toast.LENGTH_SHORT).show();
						} else {
							descuentosAgregados.add(new String[] { carnet,
									montoStr });
							Toast.makeText(MainActivity.this,
									"Descuento agregado correctamente",
									Toast.LENGTH_SHORT).show();
						}
					}
				});

		builder.setNegativeButton("Cancelar", null);
		builder.show();
	}
}
