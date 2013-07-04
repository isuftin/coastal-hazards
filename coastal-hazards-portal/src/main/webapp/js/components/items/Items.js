CCH.Objects.Items = function(args) {
	args = args ? args : {};
	var me = this === window ? {} : this;
	me.items = [];
	return $.extend(me, {
		init: function(args) {
			$(window).on('cch.search.item.submit', function(evt, data) {
				me.search({
					bbox: [data.left, data.bottom, data.right, data.top].toString(),
					query: data.keywords || '',
					type: data.themes.toString() || '',
					sortBy: data.popularity ? 'popularity' : '',
					callbacks: {
						success: [
							function(data, status, jqXHR) {
								if (data && data.items && data.items.length) {
									me.items = data.items;
									$(window).trigger('cch.data.items.loaded');
								} else {
									// TODO: Deal with when no items were returned. 
								}
							}
						],
						error: [
							function(xhr, status, error) {
								// TODO - What to do on error? Log it and somehow display it via notification
								LOG.info('An error occurred during search: ' + error);
							}
						]
					}
				});
			});
			return me;
		},
		load: function(args) {
			args = args || {};
			var items = args.items || [];

			var callbacks = args.callbacks || {
				success: [],
				error: []
			};

			if (!items.length) {
				callbacks.success.unshift(function(data, status, jqXHR) {
					me.items = data.items;
					$(window).trigger('cch.data.items.loaded');
				});
			} else {
				callbacks.success.unshift(function(data, status, jqXHR) {
					me.items.push(data);
					if (items.length) {
						me.search({
							items: items,
							callbacks: callbacks
						});
					} else {
						$(window).trigger('cch.data.items.loaded');
					}
				});
			}

			me.search({
				items: items,
				callbacks: callbacks
			});

		},
		search: function(args) {
			args = args || {};

			var count = args.count || '';
			var bbox = args.bbox || '';
			var sortBy = args.sortBy || '';
			var items = args.items || [];
			var itemId = items.pop() || '';
			var item = '/' + itemId || '';
			var query = args.query || '';
			var type = args.type || '';

			var callbacks = args.callbacks || {
				success: [],
				error: []
			};

			$.ajax({
				url: CCH.CONFIG.contextPath + CCH.CONFIG.data.sources.item.endpoint + item,
				dataType: 'json',
				data: {
					count: count,
					bbox: bbox,
					sortBy: sortBy,
					query: query,
					type: type
				},
				success: function(data, status, jqXHR) {
					callbacks.success.each(function(cb) {
						cb.apply(this, [data, status, jqXHR]);
					});
				},
				error: function(xhr, status, error) {
					callbacks.error.each(function(cb) {
						cb.apply(this, [xhr, status, error]);
					});
				}
			});
		},
		getItems: function() {
			return me.items;
		},
		getById: function(args) {
			var id = args.id;
			return me.items.find(function(item) {
				return item.id === id;
			});
		}
	});
};