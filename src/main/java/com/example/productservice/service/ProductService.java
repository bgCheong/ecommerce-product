package com.example.productservice.service;

import com.example.productservice.entity.Product;
import com.example.productservice.exception.ProductNotFoundException;
import com.example.productservice.exception.StockNotEnoughException;
import com.example.productservice.lock.DistributedLock;
import com.example.productservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {
    private final ProductRepository productRepository;
    private final DistributedLock distributedLock;

    @Transactional
    public void decreaseStock(Long productId, Integer quantity) {
        String lockKey = "product_lock:" + productId;
        // 1. 분산 락 획득
        if (!distributedLock.acquireLock(lockKey, 5)) {
            throw new RuntimeException("현재 해당 상품에 대한 요청이 많습니다. 잠시 후 다시 시도해주세요.");
        }
        log.info("Lock acquired for product: {}", productId);

        try {
            // 2. 재고 확인 및 차감 (DB 작업)
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new ProductNotFoundException("ID " + productId + "에 해당하는 상품을 찾을 수 없습니다."));

            if (product.getStock() < quantity) {
                throw new StockNotEnoughException("상품 재고가 부족합니다. (현재 재고: " + product.getStock() + ")");
            }

            product.setStock(product.getStock() - quantity);
            // @Transactional 이므로 메서드 종료 시 영속성 컨텍스트의 변경이 DB에 반영됨

        } finally {
            // 3. 락 해제
            distributedLock.releaseLock(lockKey);
            log.info("Lock released for product: {}", productId);
        }
    }
}