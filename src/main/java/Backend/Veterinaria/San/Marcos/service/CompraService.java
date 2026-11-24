
package Backend.Veterinaria.San.Marcos.service;

import Backend.Veterinaria.San.Marcos.dto.CompraRequest;
import Backend.Veterinaria.San.Marcos.model.Compra;
import Backend.Veterinaria.San.Marcos.model.DetalleCompra;
import Backend.Veterinaria.San.Marcos.model.Producto;

import Backend.Veterinaria.San.Marcos.repository.ComprasRepository;
import Backend.Veterinaria.San.Marcos.repository.DetalleCompraRepository;
import Backend.Veterinaria.San.Marcos.repository.ProductoRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Service
public class CompraService {

    private final ComprasRepository comprasRepository;
    private final DetalleCompraRepository detalleCompraRepository;
    private final ProductoRepository productoRepository;

    public CompraService(
            ComprasRepository comprasRepository,
            DetalleCompraRepository detalleCompraRepository,
            ProductoRepository productoRepository) {

        this.comprasRepository = comprasRepository;
        this.detalleCompraRepository = detalleCompraRepository;
        this.productoRepository = productoRepository;
    }

    @Transactional
    public Compra registrarCompra(CompraRequest request) {

        // 1. Validar que la compra tenga productos
        if (request == null ||
            request.productos() == null ||
            request.productos().isEmpty()) {

            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "La compra debe contener productos"
            );
        }

        // 2. Agrupar cantidades por producto
        // TreeMap ordena los IDs para bloquear siempre
        // los productos en el mismo orden.
        Map<Long, Integer> cantidades = new TreeMap<>();

        for (CompraRequest.Item item : request.productos()) {

            if (item == null ||
                item.productoId() == null ||
                item.cantidad() == null ||
                item.cantidad() <= 0) {

                throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Producto o cantidad invalida"
                );
            }

            try {
                cantidades.merge(
                    item.productoId(),
                    item.cantidad(),
                    Math::addExact
                );
            } catch (ArithmeticException e) {
                throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Cantidad demasiado grande"
                );
            }
        }

        long total = 0;
        List<DetalleCompra> detalles = new ArrayList<>();

        // 3. Consultar precios y stock real en MySQL
        for (Map.Entry<Long, Integer> entrada : cantidades.entrySet()) {

            Long productoId = entrada.getKey();
            Integer cantidad = entrada.getValue();

            Producto producto = productoRepository
                .buscarPorIdConBloqueo(productoId)
                .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Producto no encontrado: " + productoId
                ));

            // 4. Comprobar disponibilidad
            if (producto.getStock() == null ||
                producto.getStock() < cantidad) {

                throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Stock insuficiente: " + producto.getNombre()
                );
            }

            if (producto.getPrecio() == null ||
                producto.getPrecio() < 0) {

                throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Precio invalido: " + producto.getNombre()
                );
            }

            // 5. Calcular subtotal usando el precio de MySQL
            long subtotal = (long) producto.getPrecio() * cantidad;

            if (subtotal > Integer.MAX_VALUE ||
                total + subtotal > Integer.MAX_VALUE) {

                throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El total excede el valor permitido"
                );
            }

            total += subtotal;

            // 6. Descontar stock
            producto.setStock(producto.getStock() - cantidad);

            // 7. Preparar el detalle de la compra
            DetalleCompra detalle = new DetalleCompra();

            detalle.setProducto(producto);
            detalle.setCantidad(cantidad);
            detalle.setPrecioUnitario(producto.getPrecio());
            detalle.setSubtotal((int) subtotal);

            detalles.add(detalle);
        }

        // 8. Registrar la compra
        Compra compra = new Compra();
        compra.setFecha(LocalDateTime.now());
        compra.setTotal((int) total);

        Compra compraGuardada = comprasRepository.save(compra);

        // 9. Relacionar y guardar sus detalles
        for (DetalleCompra detalle : detalles) {
            detalle.setCompra(compraGuardada);
        }

        detalleCompraRepository.saveAll(detalles);

        // Los cambios de stock se guardan en la transaccion
        return compraGuardada;
    }

    // Consultar historial de compras
    public List<Compra> listarCompras() {
        return comprasRepository.findAll();
    }
}
