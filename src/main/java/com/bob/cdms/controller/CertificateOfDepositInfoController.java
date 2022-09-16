package com.bob.cdms.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bob.cdms.entity.CertificateOfDepositInfo;
import com.bob.cdms.service.ICertificateOfDepositInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

import static com.bob.cdms.constant.System.QUERY_KEY;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author zengxuan
 * @since 2022-09-10
 */
@RestController
@RequestMapping("/certificate-of-deposit-info")
@Slf4j
public class CertificateOfDepositInfoController {
    @Autowired
    private ICertificateOfDepositInfoService iCertificateOfDepositInfoService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 查看存单信息(名称搜索）
     *
     * @return List<CertificateOfDepositInfo>
     */
    @RequestMapping("/list")
    public List<CertificateOfDepositInfo> selectCertificateOfDepositInfo(@RequestParam(required = false) String CertificateOfDepositInfoName) {
        try {
            if (CertificateOfDepositInfoName == null) {
                return iCertificateOfDepositInfoService.list();
            } else {
                QueryWrapper<CertificateOfDepositInfo> queryWrapper = new QueryWrapper<>();
                queryWrapper.like("CERTIFICATES_OF_DEPOSIT_NAME", CertificateOfDepositInfoName);
                return iCertificateOfDepositInfoService.list(queryWrapper);
            }
        } catch (Exception e) {
            log.error("查询失败：{}", e.getMessage());
            throw new RuntimeException("查询失败");
        }
    }

    /**
     * 添加存单信息
     *
     * @param certificateOfDepositInfo
     * @return 状态
     */
    @RequestMapping("/add")
    @CacheEvict(value = "CDinfo", allEntries = true)
    public String addCertificateOfDepositInfo(@RequestBody CertificateOfDepositInfo certificateOfDepositInfo) {

        try {
            iCertificateOfDepositInfoService.save(certificateOfDepositInfo);
            Set<String> keys = stringRedisTemplate.keys(QUERY_KEY + "*");
            stringRedisTemplate.delete(keys);
            return "添加成功";
        } catch (Exception e) {
            log.error("添加失败：{}", e.getMessage());
            return "添加失败";
        }
    }

    /**
     * 修改存单信息
     *
     * @param certificateOfDepositInfo
     * @return 状态
     */
    @RequestMapping("/update")
    @CacheEvict(value = "CDinfo", allEntries = true)
    public String updateOrAddCertificateOfDepositInfo(@RequestBody CertificateOfDepositInfo certificateOfDepositInfo) {
        try {
            if (iCertificateOfDepositInfoService.updateById(certificateOfDepositInfo)) {
                Set<String> keys = stringRedisTemplate.keys(QUERY_KEY + "*");
                stringRedisTemplate.delete(keys);
                return "添加或成功";
            } else {
                return "添加或修改失败";
            }
        } catch (Exception e) {
            log.error("修改失败：{}", e.getMessage());
            return "操作失败,服务器内部发生错误";
        }
    }
    /**
     * 删除存单信息
     */
    @RequestMapping("/delect")
    @CacheEvict(value = "CDinfo", allEntries = true)
    public String delect(){
        try {
            if(iCertificateOfDepositInfoService.removeById(1)){
                Set<String> keys = stringRedisTemplate.keys(QUERY_KEY + "*");
                stringRedisTemplate.delete(keys);
                return "删除成功";
            }else {
                return "删除失败";
            }
        } catch (Exception e) {
            log.error("删除失败：{}", e.getMessage());
            return "删除失败";
        }
    }


}
