import http from '@/http'

export default {
    /**
	 */
    list(page, success) {
        http.get('search/list', {
            page: page
        }, success);
    },
    /**
	 */
    search(key, success) {
        http.get('search/search', {
            key: key
        }, success);
    },
    search2(key, success) {
        http.get('search/search2', {
            key: key
        }, success);
    },
   }