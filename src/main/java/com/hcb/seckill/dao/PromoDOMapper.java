package com.hcb.seckill.dao;

import com.hcb.seckill.dataobject.PromoDO;

public interface PromoDOMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table promo
     *
     * @mbg.generated Fri Oct 25 13:22:59 CST 2019
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table promo
     *
     * @mbg.generated Fri Oct 25 13:22:59 CST 2019
     */
    int insert(PromoDO record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table promo
     *
     * @mbg.generated Fri Oct 25 13:22:59 CST 2019
     */
    int insertSelective(PromoDO record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table promo
     *
     * @mbg.generated Fri Oct 25 13:22:59 CST 2019
     */
    PromoDO selectByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table promo
     *
     * @mbg.generated Fri Oct 25 13:22:59 CST 2019
     */
    int updateByPrimaryKeySelective(PromoDO record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table promo
     *
     * @mbg.generated Fri Oct 25 13:22:59 CST 2019
     */
    int updateByPrimaryKey(PromoDO record);

    PromoDO getPromoByItemId(Integer itemId);
}