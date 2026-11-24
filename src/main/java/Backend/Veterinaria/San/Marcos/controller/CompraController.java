
package Backend.Veterinaria.San.Marcos.controller;

import Backend.Veterinaria.San.Marcos.dto.CompraRequest;
import Backend.Veterinaria.San.Marcos.model.Compra;
import Backend.Veterinaria.San.Marcos.service.CompraService;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/compras")
@CrossOrigin(origins = "http://localhost:5173")
public class CompraController {

    private final CompraService compraService;

    public CompraController(CompraService compraService) {
        this.compraService = compraService;
    }

    // POST: Registrar una compra y descontar stock
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Compra registrarCompra(
            @RequestBody CompraRequest request) {

        return compraService.registrarCompra(request);
    }

    // GET: Consultar el historial de compras
    @GetMapping
    public List<Compra> listarCompras() {
        return compraService.listarCompras();
    }
}
