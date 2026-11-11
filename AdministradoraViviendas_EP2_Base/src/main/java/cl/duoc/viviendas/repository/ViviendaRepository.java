
package cl.duoc.viviendas.repository;

import cl.duoc.viviendas.model.Vivienda;
import cl.duoc.viviendas.persistence.ViviendaDAO;
import cl.duoc.viviendas.persistence.ViviendaDAOImpl;

import java.util.ArrayList;
import java.util.List;

public class ViviendaRepository {

    private final ViviendaDAO viviendaDAO;

    private final List<Vivienda> viviendas;

    public ViviendaRepository() {
        viviendaDAO = new ViviendaDAOImpl();
        viviendas = new ArrayList<>(viviendaDAO.listar());
    }

    public List<Vivienda> listar() {
        return new ArrayList<>(viviendas);
    }

    public void registrar(Vivienda vivienda) {

        if (vivienda == null) {
            throw new IllegalArgumentException(
                    "La vivienda no puede ser nula."
            );
        }

        for (Vivienda existente : viviendas) {
            if (existente.getCodigo().equalsIgnoreCase(
                    vivienda.getCodigo())) {

                throw new IllegalArgumentException(
                        "El código ya está registrado."
                );
            }
        }

        List<Vivienda> actualizadas = new ArrayList<>(viviendas);
        actualizadas.add(vivienda);

        viviendaDAO.guardar(actualizadas);

        viviendas.add(vivienda);
    }

    public void eliminar(Vivienda vivienda) {

        if (vivienda == null || !viviendas.contains(vivienda)) {
            throw new IllegalArgumentException(
                    "Debe seleccionar una vivienda válida."
            );
        }

        List<Vivienda> actualizadas = new ArrayList<>(viviendas);
        actualizadas.remove(vivienda);

        viviendaDAO.guardar(actualizadas);

        viviendas.remove(vivienda);
    }

    public List<Vivienda> buscarPorCodigo(String codigo) {

        List<Vivienda> resultados = new ArrayList<>();

        if (codigo == null) {
            return resultados;
        }

        String busqueda = codigo.trim().toLowerCase(
                java.util.Locale.ROOT
        );

        for (Vivienda vivienda : viviendas) {
            if (vivienda.getCodigo()
                    .toLowerCase(java.util.Locale.ROOT)
                    .contains(busqueda)) {

                resultados.add(vivienda);
            }
        }

        return resultados;
    }
}
