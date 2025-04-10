package com.bos.service;

import com.bos.model.dto.ResponseDto;
import com.bos.model.dto.TransReqDto;

public interface TransactionService {

    ResponseDto setor(TransReqDto dto);

    ResponseDto tarik(TransReqDto dto);

    ResponseDto transfer(TransReqDto dto);

    ResponseDto history(String accountId, String from, String to);
}
