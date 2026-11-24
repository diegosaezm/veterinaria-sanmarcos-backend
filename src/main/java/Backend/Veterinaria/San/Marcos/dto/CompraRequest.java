
package Backend.Veterinaria.San.Marcos.dto;

import java.util.List;

public record CompraRequest(
    List<Item> productos
) {
    public record Item(
        Long productoId,
        Integer cantidad
    ) {
    }
}
