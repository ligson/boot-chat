package com.yonyougov.bootchat.fw.request;


import com.yonyougov.bootchat.fw.filter.FilterOrder;
import com.yonyougov.bootchat.fw.request.dto.RequestHeader;
import com.yonyougov.bootchat.fw.request.dto.RequestHeaders;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;


import java.io.IOException;
import java.util.Enumeration;
import java.util.Optional;


@Slf4j
@Component
public class RequestStore {

    private static final ThreadLocal<RequestHeaders> headersStore = new InheritableThreadLocal<>();
    private static final ThreadLocal<HttpServletRequest> requestStore = new InheritableThreadLocal<>();

    @Order(FilterOrder.SERVLET_FILTER_REQUEST_ORDER)
    @Component
    static class CruxRequestFilter extends OncePerRequestFilter {

        private final CruxRequestFilterWrapper cruxRequestFilterWrapper;

        CruxRequestFilter(CruxRequestFilterWrapper cruxRequestFilterWrapper) {
            this.cruxRequestFilterWrapper = cruxRequestFilterWrapper;
        }

        @Override
        protected void doFilterInternal(HttpServletRequest request,
                                        HttpServletResponse response,
                                        FilterChain filterChain) throws ServletException, IOException {

            String requestURI = request.getRequestURI();
            if (!cruxRequestFilterWrapper.isNeedStatistics(requestURI)) {
                filterChain.doFilter(request, response);
                return;
            }

            try {
                Enumeration<String> enums = request.getHeaderNames();
                RequestHeaders headers = new RequestHeaders();
                while (enums.hasMoreElements()) {
                    String name = enums.nextElement();
                    String value = request.getHeader(name);
                    RequestHeader header = new RequestHeader();
                    header.setName(name);
                    header.setValue(value);
                    headers.getHeaders().add(header);
                }
                headersStore.set(headers);
                requestStore.set(request);
                filterChain.doFilter(request, response);
            } finally {
                headersStore.remove();
                requestStore.remove();
            }
        }
    }

    public RequestHeaders getRequestHeaders() {
        return headersStore.get();
    }

    public String getHeader(String name) {
        RequestHeaders headers = headersStore.get();
        if(headers == null){
            return null;
        }else{
            Optional<RequestHeader> first =
                    headers.getHeaders().stream().filter(h -> name.equalsIgnoreCase(h.getName())).findFirst();
            return first.map(RequestHeader::getValue).orElse(null);
        }
    }

    public String getSessionId() {
        HttpServletRequest httpServletRequest = requestStore.get();
        return Optional.of(httpServletRequest.getSession().getId()).orElse(null);
    }
}
