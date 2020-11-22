package com.example.mahitabstockkeeping.managers;

import com.shopify.buy3.Storefront;
import com.shopify.graphql.support.ID;

public class ClientQuery {

    static Storefront.QueryRootQuery queryProducts(ID collectionID) {
        return Storefront.query(
                rootQuery -> rootQuery
                        .node(collectionID,
                                nodeQuery -> nodeQuery
                                        .onCollection(
                                                collectionQuery -> collectionQuery
                                                        .products(
                                                                arg -> arg.first(25),
                                                                productsQuery -> productsQuery
                                                                        .pageInfo(
                                                                                productPageQuery -> productPageQuery
                                                                                        .hasNextPage()
                                                                        )
                                                                        .edges(
                                                                                productEdgeQuery -> productEdgeQuery
                                                                                        .cursor()
                                                                                        .node(
                                                                                                productNodeQuery -> productNodeQuery
                                                                                                        .title()
                                                                                                        .descriptionHtml()
                                                                                                        .publishedAt()
                                                                                                        .updatedAt()
                                                                                                        .tags()
                                                                                                        .images(
                                                                                                                args -> args
                                                                                                                        .first(25),
                                                                                                                image -> image
                                                                                                                        .edges(
                                                                                                                                edge -> edge
                                                                                                                                        .node(
                                                                                                                                                node -> node
                                                                                                                                                        .src()
                                                                                                                                        )
                                                                                                                        )
                                                                                                        )
                                                                                                        .variants(
                                                                                                                args -> args.first(25),
                                                                                                                variantsQuery -> variantsQuery
                                                                                                                        .pageInfo(
                                                                                                                                variantsPageQuery -> variantsPageQuery
                                                                                                                                        .hasNextPage()
                                                                                                                        )
                                                                                                                        .edges(
                                                                                                                                variantsEdgeQuery -> variantsEdgeQuery
                                                                                                                                        .cursor()
                                                                                                                                        .node(
                                                                                                                                                variantsEdgeNodeQuery -> variantsEdgeNodeQuery
                                                                                                                                                        .title()
                                                                                                                                                        .price()
                                                                                                                                                        .compareAtPrice()
                                                                                                                                                        .sku()
                                                                                                                                                        .weight()
                                                                                                                                                        .weightUnit()
                                                                                                                                                        .availableForSale()
                                                                                                                                                        .selectedOptions(
                                                                                                                                                                opt -> opt
                                                                                                                                                                        .name()
                                                                                                                                                                        .value()
                                                                                                                                                        )
                                                                                                                                        )
                                                                                                                        )
                                                                                                        )
                                                                                        )
                                                                        )
                                                        )

                                        )
                        )
        );
    }
}
