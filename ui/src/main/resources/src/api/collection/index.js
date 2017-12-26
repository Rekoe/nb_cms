import http from '@/http'

export default {
    list(page, success) {
        http.get('collection/list', {
            page: page
        }, success);
    },
    search(collection, success) {
    		http.postBody('collection/search', collection, success)
    }
}