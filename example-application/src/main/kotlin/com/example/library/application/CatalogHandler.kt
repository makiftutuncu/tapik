package com.example.library.application

import com.example.library.contract.Catalog
import com.example.library.generated.CatalogServer
import org.springframework.stereotype.Component

@Component
class CatalogHandler : CatalogServer {
    override val catalogApi: Catalog = Catalog()

    override fun list(): CatalogServer.ListResponse = CatalogServer.ListResponse.Ok
}
