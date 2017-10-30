package com.twh.annotation;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

public class HttpServerImportSelector implements ImportSelector {
    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        return new String[]{HttpServerConfiguration.class.getName()};
    }
}
