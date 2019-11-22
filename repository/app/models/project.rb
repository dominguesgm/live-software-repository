class Project < ApplicationRecord
    has_many :packages, dependent: :destroy
    has_many :events, dependent: :destroy
end
