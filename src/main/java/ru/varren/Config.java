package ru.varren;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.ResourceResolver;
import org.springframework.web.servlet.resource.ResourceResolverChain;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Configuration
public class Config implements WebMvcConfigurer {


    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        ResourceResolver resolver = new ReactResourceResolver();
        registry.addResourceHandler("/**")
                .resourceChain(true)
                .addResolver(resolver);

        // Can try to play with
        // registry.addResourceHandler("/**")
        //        .addResourceLocations("classpath:/static/");
        // But this option can't map every path to index.html
        // Can try https://stackoverflow.com/a/42998817/1032167
        // to resolve this, but then you loose /api/** => rest
        // and to be honest it is some regex madness, so
        // it was easier for me to setup custom resource resolver


    }

    public class ReactResourceResolver implements ResourceResolver {
        // this is the same directory you are using in package.json
        // "build-spring": "react-scripts build &&  mv build static && mv static ../src/main/resources",
        // example REACT_DIR/index.html
        private static final String REACT_DIR = "/static/";

        // this is directory inside REACT_DIR for react static files
        // example REACT_DIR/REACT_STATIC_DIR/js/
        // example REACT_DIR/REACT_STATIC_DIR/css/
        private static final String REACT_STATIC_DIR = "static";

        private Resource index = new ClassPathResource(REACT_DIR + "index.html");
        private List<String> rootStaticFiles = Arrays.asList("favicon.io",
                "asset-manifest.json", "manifest.json", "service-worker.js");

        @Override
        public Resource resolveResource(HttpServletRequest request, String requestPath,
                                        List<? extends Resource> locations, ResourceResolverChain chain) {
            return resolve(requestPath, locations);
        }

        @Override
        public String resolveUrlPath(String resourcePath, List<? extends Resource> locations, ResourceResolverChain chain) {
            Resource resolvedResource = resolve(resourcePath, locations);
            if (resolvedResource == null) {
                return null;
            }
            try {
                return resolvedResource.getURL().toString();
            } catch (IOException e) {
                return resolvedResource.getFilename();
            }
        }

        private Resource resolve(String requestPath, List<? extends Resource> locations) {

            if (requestPath == null) return null;

            if (rootStaticFiles.contains(requestPath)
                    || requestPath.startsWith(REACT_STATIC_DIR)) {
                return new ClassPathResource(REACT_DIR + requestPath);
            } else
                return index;
        }

    }

}
