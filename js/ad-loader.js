

const AD_API_URL = 'http://localhost:8080/ad-management/api/ads';

const RECOMMEND_API_URL = '/shopping-web/api/ad/recommend';

document.addEventListener('DOMContentLoaded', function() {
    loadAds();
});


function loadAds() {
    const adContainer = document.getElementById('ad-container');

    if (!adContainer) {
        return;
    }


    fetchRecommendation()
        .then(preference => {

            return fetchAds(preference);
        })
        .then(ads => {
            displayAd(ads, adContainer);
        })
        .catch(error => {
            console.error('广告加载失败:', error);
            displayDefaultAd(adContainer);
        });
}


function fetchRecommendation() {
    return fetch(RECOMMEND_API_URL)
        .then(response => response.json())
        .then(data => {
            console.log('用户偏好:', data);
            return data;
        })
        .catch(error => {
            console.warn('无法获取推荐偏好:', error);
            return {};
        });
}


function fetchAds(preference) {

    const params = new URLSearchParams();

    if (preference.categoryId) {
        params.append('categoryId', preference.categoryId);
    }

    const url = `${AD_API_URL}?${params.toString()}`;

    return fetch(url)
        .then(response => {
            if (!response.ok) {
                throw new Error('广告API响应失败');
            }
            return response.json();
        })
        .then(data => {
            if (data && data.ads && data.ads.length > 0) {
                return data.ads;
            }
            return [];
        })
        .catch(error => {
            console.warn('从广告服务器获取广告失败，使用模拟数据:', error);

            return getMockAds(preference);
        });
}


function getMockAds(preference) {
    const mockAds = {
        1: {
            type: 'image',
            title: '最新iPhone 15 Pro - 限时优惠',
            content: '立减1000元，限量抢购！',
            imageUrl: 'https://via.placeholder.com/1200x300/667eea/ffffff?text=iPhone+15+Pro+限时优惠',
            link: '#'
        },
        2: {
            type: 'image',
            title: 'YSL限量版口红 - 新品上市',
            content: '经典色号，一抹倾心',
            imageUrl: 'https://via.placeholder.com/1200x300/f093fb/ffffff?text=YSL+限量版口红',
            link: '#'
        },
        3: {
            type: 'image',
            title: '冬季新款羽绒服 - 保暖又时尚',
            content: '全场5折起，买一送一',
            imageUrl: 'https://via.placeholder.com/1200x300/4facfe/ffffff?text=冬季羽绒服大促',
            link: '#'
        },
        default: {
            type: 'image',
            title: '精品购物商城 - 品质生活从这里开始',
            content: '新用户专享优惠券，立即领取',
            imageUrl: 'https://via.placeholder.com/1200x300/00f2fe/ffffff?text=欢迎来到精品购物商城',
            link: '#'
        }
    };

    const categoryId = preference.categoryId || 'default';
    const ad = mockAds[categoryId] || mockAds.default;

    return [ad];
}


function displayAd(ads, container) {
    if (!ads || ads.length === 0) {
        displayDefaultAd(container);
        return;
    }


    const ad = ads[Math.floor(Math.random() * ads.length)];

    let adHtml = '';

    switch (ad.type) {
        case 'text':
            adHtml = createTextAd(ad);
            break;
        case 'image':
            adHtml = createImageAd(ad);
            break;
        case 'video':
            adHtml = createVideoAd(ad);
            break;
        default:
            adHtml = createImageAd(ad);
    }

    container.innerHTML = adHtml;


    const adLink = container.querySelector('.ad-link');
    if (adLink) {
        adLink.addEventListener('click', function(e) {
            trackAdClick(ad);
        });
    }
}


function createTextAd(ad) {
    return `
        <div class="ad-text">
            <h3 style="font-size: 24px; margin-bottom: 10px;">${ad.title}</h3>
            <p style="font-size: 16px;">${ad.content}</p>
            ${ad.link ? `<a href="${ad.link}" class="ad-link" style="display: inline-block; margin-top: 15px; padding: 10px 30px; background: white; color: #667eea; border-radius: 25px; text-decoration: none; font-weight: bold;">了解更多 →</a>` : ''}
        </div>
    `;
}


function createImageAd(ad) {
    return `
        <div class="ad-image">
            ${ad.link ? `<a href="${ad.link}" class="ad-link">` : ''}
                <img src="${ad.imageUrl}" alt="${ad.title}" style="width: 100%; height: auto; border-radius: 10px; display: block;">
            ${ad.link ? `</a>` : ''}
            <div style="position: absolute; bottom: 20px; left: 20px; right: 20px;">
                <h3 style="font-size: 24px; margin-bottom: 5px; text-shadow: 2px 2px 4px rgba(0,0,0,0.5);">${ad.title}</h3>
                <p style="font-size: 16px; text-shadow: 1px 1px 2px rgba(0,0,0,0.5);">${ad.content}</p>
            </div>
        </div>
    `;
}


function createVideoAd(ad) {
    return `
        <div class="ad-video">
            <video controls autoplay muted style="width: 100%; border-radius: 10px;">
                <source src="${ad.videoUrl}" type="video/mp4">
                您的浏览器不支持视频标签。
            </video>
            <div style="margin-top: 10px;">
                <h3 style="font-size: 20px; color: white;">${ad.title}</h3>
                <p style="font-size: 14px; color: rgba(255,255,255,0.9);">${ad.content}</p>
            </div>
        </div>
    `;
}


function displayDefaultAd(container) {
    container.innerHTML = `
        <div class="ad-default">
            <i class="fas fa-shopping-bag" style="font-size: 60px; margin-bottom: 15px;"></i>
            <h3 style="font-size: 28px; margin-bottom: 10px;">精品购物商城</h3>
            <p style="font-size: 18px;">发现更多精彩商品</p>
        </div>
    `;
}


function trackAdClick(ad) {
    console.log('广告被点击:', ad);


}

