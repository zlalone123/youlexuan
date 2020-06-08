package com.zl.shop.service;

import com.zl.pojo.TbSeller;
import com.zl.sellergoods.service.SellerService;
import jdk.nashorn.internal.ir.annotations.Reference;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

public class UserDetailServiceimpl implements UserDetailsService {

    SellerService sellerService;

    public void setSellerService(SellerService sellerService) {
        this.sellerService = sellerService;
    }

    @Override
    public UserDetails loadUserByUsername(String sellerId) throws UsernameNotFoundException {

        //根据前段输入的用户名，去数据库中查询到这个用户名对应的信息
        List<GrantedAuthority> grantedAuthoritys = new ArrayList<>();
        //用户对应的角色
        grantedAuthoritys.add(new SimpleGrantedAuthority("ROLE_SELLER"));

        //todo(根据用户名去数据库查询对应的用户信息)
        TbSeller tbSeller = sellerService.findOne(sellerId);

        if(ObjectUtils.isEmpty(tbSeller)){
            return null;
        }

        if ("1".equals(tbSeller.getStatus())){
            return new User(sellerId,tbSeller.getPassword(),grantedAuthoritys);
        }else {
            return null;
        }

    }
}
