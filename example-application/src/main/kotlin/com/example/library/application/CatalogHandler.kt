package com.example.library.application

import com.example.library.contract.Catalog
import com.example.library.generated.contract.Catalog.CatalogServer
import com.example.library.generated.contract.Catalog.ListResponse
import org.springframework.stereotype.Component

@Component
class CatalogHandler : CatalogServer {
    override val catalogApi: Catalog = Catalog()

    override fun list(): ListResponse = ListResponse.Ok
}
