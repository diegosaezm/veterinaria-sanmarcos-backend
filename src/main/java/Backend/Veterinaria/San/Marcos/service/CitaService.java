
package Backend.Veterinaria.San.Marcos.service;

import Backend.Veterinaria.San.Marcos.model.Cita;
import Backend.Veterinaria.San.Marcos.repository.CitaRepository;

import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class CitaService {

    private final CitaRepository citaRepository;

    public CitaService(CitaRepository citaRepository) {
        this.citaRepository = citaRepository;
    }

    // Obtener todas las citas
    public List<Cita> listarCitas() {
        return citaRepository.findAll();
    }

    // Buscar una cita por su ID
    public Cita obtenerCita(Long id) {
        return citaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Cita no encontrada"));
    }

    // Crear una nueva cita
    public Cita crearCita(Cita cita) {
        cita.setId(null);
        return citaRepository.save(cita);
    }

    // Actualizar una cita existente
    public Cita actualizarCita(Long id, Cita datos) {

        Cita cita = obtenerCita(id);

        cita.setNombre(datos.getNombre());
        cita.setMascota(datos.getMascota());
        cita.setServicio(datos.getServicio());
        cita.setFecha(datos.getFecha());

        return citaRepository.save(cita);
    }

    // Eliminar una cita
    public void eliminarCita(Long id) {
        Cita cita = obtenerCita(id);
        citaRepository.delete(cita);
    }
}
