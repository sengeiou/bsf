package com.weking.mapper.pocket;

import com.weking.model.pocket.AppleReceipt;

public interface AppleReceiptMapper {

    int insert(AppleReceipt record);

    int selectCountByReceipt(String receipt);

}