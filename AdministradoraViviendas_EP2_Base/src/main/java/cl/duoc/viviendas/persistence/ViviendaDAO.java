
package cl.duoc.viviendas.persistence;

import cl.duoc.viviendas.model.Vivienda;
import java.util.List;

public interface ViviendaDAO {

    List<Vivienda> listar();

    void guardar(List<Vivienda> viviendas);
}
