
package Backend.Veterinaria.San.Marcos.service;

import Backend.Veterinaria.San.Marcos.model.Producto;
import Backend.Veterinaria.San.Marcos.repository.ProductoRepository;

import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;

    public ProductoService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    // READ: Listar todos los productos
    public List<Producto> listarProductos() {
        return productoRepository.findAll();
    }

    // READ: Buscar un producto por ID
    public Producto obtenerProducto(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Producto no encontrado"
                ));
    }

    // CREATE: Guardar un nuevo producto
    public Producto crearProducto(Producto producto) {
        producto.setId(null);
        return productoRepository.save(producto);
    }

    // UPDATE: Modificar un producto existente
    public Producto actualizarProducto(Long id, Producto datos) {

        Producto producto = obtenerProducto(id);

        producto.setNombre(datos.getNombre());
        producto.setPrecio(datos.getPrecio());
        producto.setDescripcion(datos.getDescripcion());
        producto.setImagen(datos.getImagen());
        producto.setStock(datos.getStock());

        return productoRepository.save(producto);
    }

    // DELETE: Eliminar un producto
    public void eliminarProducto(Long id) {
        Producto producto = obtenerProducto(id);
        productoRepository.delete(producto);
    }
}
