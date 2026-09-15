package com.example.gestiondeventasonlinestore_dany

object UbicacionColombia {

    private val mapa = linkedMapOf(
        "Amazonas" to listOf("Leticia", "Puerto Nariño"),
        "Antioquia" to listOf("Medellín", "Bello", "Envigado", "Itagüí", "Sabaneta", "Rionegro", "Apartadó", "Turbo"),
        "Arauca" to listOf("Arauca", "Tame", "Saravena"),
        "Atlántico" to listOf("Barranquilla", "Soledad", "Malambo", "Puerto Colombia", "Sabanalarga"),
        "Bogotá D.C." to listOf("Bogotá D.C."),
        "Bolívar" to listOf("Cartagena", "Magangué", "Turbaco", "Arjona", "El Carmen de Bolívar"),
        "Boyacá" to listOf("Tunja", "Duitama", "Sogamoso", "Paipa", "Chiquinquirá"),
        "Caldas" to listOf("Manizales", "Villamaría", "Chinchiná", "La Dorada"),
        "Caquetá" to listOf("Florencia", "San Vicente del Caguán", "El Doncello"),
        "Casanare" to listOf("Yopal", "Aguazul", "Villanueva"),
        "Cauca" to listOf("Popayán", "Santander de Quilichao", "Puerto Tejada", "Piendamó"),
        "Cesar" to listOf("Valledupar", "Aguachica", "Codazzi"),
        "Chocó" to listOf("Quibdó", "Istmina", "Condoto"),
        "Córdoba" to listOf("Montería", "Cereté", "Lorica", "Sahagún"),
        "Cundinamarca" to listOf("Soacha", "Zipaquirá", "Chía", "Cajicá", "Mosquera", "Funza", "Madrid"),
        "Guainía" to listOf("Inírida"),
        "Guaviare" to listOf("San José del Guaviare", "El Retorno"),
        "Huila" to listOf("Neiva", "Pitalito", "Garzón", "La Plata"),
        "La Guajira" to listOf("Riohacha", "Maicao", "Uribia"),
        "Magdalena" to listOf("Santa Marta", "Ciénaga", "Fundación", "El Banco"),
        "Meta" to listOf("Villavicencio", "Acacías", "Granada", "Puerto López"),
        "Nariño" to listOf("Pasto", "Tumaco", "Ipiales", "Túquerres"),
        "Norte de Santander" to listOf("Cúcuta", "Ocaña", "Los Patios", "Villa del Rosario"),
        "Putumayo" to listOf("Mocoa", "Puerto Asís", "Orito"),
        "Quindío" to listOf("Armenia", "Calarcá", "Montenegro", "La Tebaida"),
        "Risaralda" to listOf("Pereira", "Dosquebradas", "Santa Rosa de Cabal", "La Virginia"),
        "San Andrés y Providencia" to listOf("San Andrés"),
        "Santander" to listOf("Bucaramanga", "Floridablanca", "Girón", "Piedecuesta", "Barrancabermeja", "San Gil"),
        "Sucre" to listOf("Sincelejo", "Corozal", "Tolú", "Sampués"),
        "Tolima" to listOf("Ibagué", "Espinal", "Honda", "Melgar"),
        "Valle del Cauca" to listOf("Cali", "Palmira", "Buga", "Tuluá", "Cartago", "Yumbo", "Buenaventura"),
        "Vaupés" to listOf("Mitú"),
        "Vichada" to listOf("Puerto Carreño", "Cumaribo")
    )

    fun obtener(): Map<String, List<String>> = mapa
}