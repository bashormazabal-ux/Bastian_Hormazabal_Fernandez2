
package cl.duoc.viviendas.persistence;

import cl.duoc.viviendas.model.Casa;
import cl.duoc.viviendas.model.Departamento;
import cl.duoc.viviendas.model.Vivienda;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ViviendaDAOImpl implements ViviendaDAO {

    private static final Path ARCHIVO_CASAS =
            Paths.get("casas.json");

    private static final Path ARCHIVO_DEPARTAMENTOS =
            Paths.get("departamentos.json");

    private final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    @Override
    public List<Vivienda> listar() {
        List<Vivienda> viviendas = new ArrayList<>();

        Type tipoCasas = new TypeToken<List<Casa>>() {}.getType();
        Type tipoDepartamentos =
                new TypeToken<List<Departamento>>() {}.getType();

        viviendas.addAll(
                leerArchivo(ARCHIVO_CASAS, tipoCasas)
        );

        viviendas.addAll(
                leerArchivo(ARCHIVO_DEPARTAMENTOS, tipoDepartamentos)
        );

        return viviendas;
    }

    private <T extends Vivienda> List<T> leerArchivo(
            Path archivo, Type tipo) {

        if (!Files.exists(archivo)) {
            return new ArrayList<>();
        }

        try (Reader reader = Files.newBufferedReader(
                archivo, StandardCharsets.UTF_8)) {

            List<T> datos = gson.fromJson(reader, tipo);

            return datos != null ? datos : new ArrayList<>();

        } catch (IOException | RuntimeException e) {
            throw new IllegalStateException(
                    "No se pudo leer " + archivo + ": "
                            + e.getMessage(), e
            );
        }
    }

    @Override
    public void guardar(List<Vivienda> viviendas) {

        List<Casa> casas = new ArrayList<>();
        List<Departamento> departamentos = new ArrayList<>();

        for (Vivienda vivienda : viviendas) {
            if (vivienda instanceof Casa casa) {
                casas.add(casa);
            } else if (vivienda instanceof Departamento departamento) {
                departamentos.add(departamento);
            }
        }

        escribirArchivo(ARCHIVO_CASAS, casas);
        escribirArchivo(ARCHIVO_DEPARTAMENTOS, departamentos);
    }

    private void escribirArchivo(Path archivo, Object datos) {

        try (Writer writer = Files.newBufferedWriter(
                archivo, StandardCharsets.UTF_8)) {

            gson.toJson(datos, writer);

        } catch (IOException e) {
            throw new IllegalStateException(
                    "No se pudo guardar " + archivo + ": "
                            + e.getMessage(), e
            );
        }
    }
}
