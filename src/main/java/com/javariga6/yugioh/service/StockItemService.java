package com.javariga6.yugioh.service;

import com.javariga6.yugioh.exceptions.ReferencedResourceNotFoundException;
import com.javariga6.yugioh.exceptions.ResourceNotFoundException;
import com.javariga6.yugioh.model.Article;
import com.javariga6.yugioh.model.CardStorage;
import com.javariga6.yugioh.model.StockItem;
import com.javariga6.yugioh.repository.ArticleRepository;
import com.javariga6.yugioh.repository.CardStorageRepository;
import com.javariga6.yugioh.repository.StockItemRepository;
import com.sun.istack.NotNull;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StockItemService {
    private final StockItemRepository stockItemRepository;
    private final ArticleRepository articleRepository;
    private final CardStorageRepository cardStorageRepository;

    public StockItemService(StockItemRepository stockItemRepository, ArticleRepository articleRepository, CardStorageRepository cardStorageRepository) {
        this.stockItemRepository = stockItemRepository;
        this.articleRepository = articleRepository;
        this.cardStorageRepository = cardStorageRepository;
    }

    public StockItem saveStockItem(StockItem stockItem) {
        if (stockItem.getArticle()==null || stockItem.getCardStorage() == null || stockItem.getArticle().getId() == null || stockItem.getCardStorage().getId() == null) {
            throw new ReferencedResourceNotFoundException();
        }
        Article article = articleRepository.findById(stockItem.getArticle().getId())
                .orElseThrow(ReferencedResourceNotFoundException::new);
        CardStorage cardStorage = cardStorageRepository.findById(stockItem.getCardStorage().getId())
                .orElseThrow(ReferencedResourceNotFoundException::new);
        stockItem.setArticle(article);
        stockItem.setCardStorage(cardStorage);
        return stockItemRepository.save(stockItem);
    }


    public void delete(StockItem stockItem) {
        stockItemRepository.delete(stockItem);
    }

    public void deleteStockItemById(Long id) {
        stockItemRepository.deleteById(id);
    }

    public void findStockItemById(Long id) {
        stockItemRepository.findById(id);
    }


    public List<StockItem> findAllStockItems() {
        return stockItemRepository.findAll();
    }


    public StockItem updateStockItem(@NotNull StockItem stockItem) {
        StockItem stockItemOriginal = stockItemRepository
                .findById(stockItem.getId())
                .orElseThrow(ResourceNotFoundException::new);
        stockItem.setArticle(stockItemOriginal.getArticle());
        return stockItemRepository.save(stockItem);
    }


}

