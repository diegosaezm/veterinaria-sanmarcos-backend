
package Backend.Veterinaria.San.Marcos.controller;

import Backend.Veterinaria.San.Marcos.model.Cita;
import Backend.Veterinaria.San.Marcos.service.CitaService;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

@RestController
@RequestMapping("/api/v1/citas")
@CrossOrigin(origins = "http://localhost:5173")
public class CitaController {

    private final CitaService citaService;

    public CitaController(CitaService citaService) {
        this.citaService = citaService;
    }

    @GetMapping
    public List<Cita> listarCitas() {
        return citaService.listarCitas();
    }

    @GetMapping("/{id}")
    public Cita obtenerCita(@PathVariable Long id) {
        return citaService.obtenerCita(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Cita crearCita(@RequestBody Cita cita) {
        return citaService.crearCita(cita);
    }

    @PutMapping("/{id}")
    public Cita actualizarCita(
            @PathVariable Long id,
            @RequestBody Cita cita) {
        return citaService.actualizarCita(id, cita);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminarCita(@PathVariable Long id) {
        citaService.eliminarCita(id);
    }
}
