class Event < ApplicationRecord
    has_many :event_arguments, dependent: :destroy
    belongs_to :project
end
