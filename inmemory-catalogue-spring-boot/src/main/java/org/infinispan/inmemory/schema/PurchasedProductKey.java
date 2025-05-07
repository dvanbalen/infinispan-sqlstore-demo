package org.infinispan.inmemory.schema;

import org.infinispan.protostream.annotations.Proto;
import org.infinispan.protostream.annotations.ProtoFactory;
import org.infinispan.protostream.annotations.ProtoField;

@Proto
public record PurchasedProductKey(
        @ProtoField(number = 1, name = "id")
        Long commandId,
        @ProtoField(number = 2, name = "products_id")
        Long productId) {
}
