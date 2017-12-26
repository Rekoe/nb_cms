import http from '@/http'

export default {
    /**
	 */
    load(url, success) {
        http.get(url, success);
    },
    /**
	 */
    commit(url,data, success) {
    		http.postBody(url,data, data, success)
    }
}